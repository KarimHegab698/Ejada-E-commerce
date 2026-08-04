package com.example.shop_service.service;

import com.example.shop_service.client.ProductClient;
import com.example.shop_service.client.ProductInfoResponse;
import com.example.shop_service.dto.AddCartItemRequest;
import com.example.shop_service.dto.CartItemResponse;
import com.example.shop_service.dto.CartResponse;
import com.example.shop_service.dto.UpdateQuantityRequest;
import com.example.shop_service.entity.Cart;
import com.example.shop_service.entity.CartItem;
import com.example.shop_service.exception.CartNotFoundException;
import com.example.shop_service.exception.CheckoutFailedException;
import com.example.shop_service.repository.CartItemRepository;
import com.example.shop_service.repository.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductClient productClient;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, ProductClient productClient) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productClient = productClient;
    }

    private Cart createCart(Long userId) {
        return cartRepository.save(Cart.builder().userId(userId).status(Cart.Status.ACTIVE).build());
    }

    private CartResponse toResponse(Cart cart) {
        BigDecimal total = cart.getItems().stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .status(cart.getStatus().name())
                .items(cart.getItems().stream().map(i -> CartItemResponse.builder()
                        .id(i.getId())
                        .productId(i.getProductId())
                        .quantity(i.getQuantity())
                        .unitPrice(i.getUnitPrice())
                        .build()).toList())
                .total(total)
                .build();
    }

    public CartResponse getActiveCart(Long userId){
        Cart cart = cartRepository.findByUserIdAndStatus(userId, Cart.Status.ACTIVE)
                .orElseGet(() -> createCart(userId));
        return toResponse(cart);
    }

    @Transactional
    public CartResponse addItem(Long userId, AddCartItemRequest request){
        Cart cart = cartRepository.findByUserIdAndStatus(userId, Cart.Status.ACTIVE)
                .orElseGet(() -> createCart(userId));

        ProductInfoResponse product = productClient.getProduct(request.getProductId());
        if(product == null){
            throw new CheckoutFailedException("Could not fetch product info from inventory-service");
        }

        CartItem item = CartItem.builder()
                .cart(cart)
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .unitPrice(product.getPrice())
                .build();
        cart.getItems().add(item);
        cartItemRepository.save(item);
        return toResponse(cart);
    }

    @Transactional
    public CartResponse removeItem(Long userId, Long cartItemId){
        Cart cart = cartRepository.findByUserIdAndStatus(userId, Cart.Status.ACTIVE)
                .orElseThrow(() -> new CartNotFoundException("No active cart for user " + userId));

        cart.getItems().removeIf(item -> item.getId().equals(cartItemId));
        cartItemRepository.deleteById(cartItemId);
        return toResponse(cart);
    }

    @Transactional
    public CartResponse updateItemQuantity(Long userId, Long cartItemId, UpdateQuantityRequest request) {
        Cart cart = cartRepository.findByUserIdAndStatus(userId, Cart.Status.ACTIVE)
                .orElseThrow(() -> new CartNotFoundException("No active cart for user " + userId));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new CartNotFoundException("Cart item not found: " + cartItemId));

        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);

        return toResponse(cart);
    }
}
