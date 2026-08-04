package com.example.shop_service.service;

import com.example.shop_service.client.*;
import com.example.shop_service.dto.CartItemResponse;
import com.example.shop_service.dto.OrderResponse;
import com.example.shop_service.entity.*;
import com.example.shop_service.exception.CartNotFoundException;
import com.example.shop_service.exception.CheckoutFailedException;
import com.example.shop_service.exception.OrderNotFoundException;
import com.example.shop_service.repository.CartRepository;
import com.example.shop_service.repository.OrderRepository;
import com.example.shop_service.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final InventoryGateway inventoryClient;
    private final WalletGateway walletClient;


    public OrderService(CartRepository cartRepository, OrderRepository orderRepository, PaymentRepository paymentRepository, InventoryGateway inventoryClient, WalletGateway walletClient) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.inventoryClient = inventoryClient;
        this.walletClient = walletClient;
    }

    public OrderResponse getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));
        return toResponse(order);
    }

    private void restoreAll(Order order, List<OrderItem> items) {
        for (OrderItem item : items) {
            inventoryClient.adjustStock(
                    item.getProductId(),
                    new StockAdjustRequest(
                            item.getQuantity(),
                            String.valueOf(order.getId()),
                            StockAdjustRequest.StockOperation.RESTORE
                    )
            );
        }
    }

    private OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .items(order.getItems().stream().map(i -> CartItemResponse.builder()
                        .id(i.getId())
                        .productId(i.getProductId())
                        .quantity(i.getQuantity())
                        .unitPrice(i.getUnitPrice())
                        .build()).toList())
                .build();
    }

    @Transactional
    public OrderResponse checkout(Long userId){
        Cart cart = cartRepository.findByUserIdAndStatus(userId, Cart.Status.ACTIVE)
                .orElseThrow(() -> new CartNotFoundException("No active cart for user " + userId));

        if(cart.getItems().isEmpty()){
            throw new CheckoutFailedException("Cannot checkout an empty cart");
        }

        BigDecimal total = cart.getItems().stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .userId(userId)
                .status(Order.Status.CREATED)
                .totalAmount(total)
                .build();

        for(CartItem cartItem : cart.getItems()){
            order.getItems().add(OrderItem.builder()
                    .order(order)
                    .productId(cartItem.getProductId())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getUnitPrice())
                    .build());
        }
        order = orderRepository.save(order);

        List<OrderItem> deducted = new ArrayList<>();
        for (OrderItem item : order.getItems()){
            try{
                inventoryClient.adjustStock(
                        item.getProductId(),
                        new StockAdjustRequest(
                                item.getQuantity(),
                                String.valueOf(order.getId()),
                                StockAdjustRequest.StockOperation.DEDUCT
                        )
                );
                deducted.add(item);
            } catch (Exception ex) {
                restoreAll(order, deducted);
                order.setStatus(Order.Status.FAILED);
                orderRepository.save(order);
                throw new CheckoutFailedException("Stock deduction failed for product " + item.getProductId() + ": " + ex.getMessage());
            }
        }

        WalletInfoResponse wallet = walletClient.getWalletByUserId(userId);
        if(wallet == null) {
            restoreAll(order, deducted);
            order.setStatus(Order.Status.FAILED);
            orderRepository.save(order);
            throw new CheckoutFailedException("Could not reach wallet-service to charge user " + userId);
        }

        WalletPaymentResponse paymentResult;
        try {
            paymentResult = walletClient.pay(wallet.getId(),
                    new WalletPaymentRequest(total, String.valueOf(order.getId())));
        } catch (Exception ex) {
            restoreAll(order, deducted);
            order.setStatus(Order.Status.FAILED);
            orderRepository.save(order);
            throw new CheckoutFailedException("Payment failed for order " + order.getId() + ": " + ex.getMessage());
        }

        Payment payment = Payment.builder()
                .orderId(order.getId())
                .walletTransactionRef(paymentResult.getTransactionId() == null ? null : paymentResult.getTransactionId().toString())
                .amount(total)
                .build();

        if(!"COMPLETED".equals(paymentResult.getStatus())){
            restoreAll(order, deducted);
            payment.setStatus(Payment.Status.FAILED);
            paymentRepository.save(payment);

            order.setStatus(Order.Status.FAILED);
            orderRepository.save(order);
            throw new CheckoutFailedException("Payment failed for order " + order.getId());
        }

        payment.setStatus(Payment.Status.SUCCESS);
        paymentRepository.save(payment);

        order.setStatus(Order.Status.PAID);
        orderRepository.save(order);

        cart.setStatus(Cart.Status.CHECKED_OUT);
        cartRepository.save(cart);

        return toResponse(order);
    }
}
