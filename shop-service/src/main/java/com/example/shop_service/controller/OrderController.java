package com.example.shop_service.controller;

import com.example.shop_service.dto.OrderResponse;
import com.example.shop_service.security.CurrentUser;
import com.example.shop_service.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> checkout(){
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.checkout(CurrentUser.id()));
    }

    @GetMapping("/{orderId}")
    public OrderResponse getOrder(@PathVariable Long orderId){
        return orderService.getOrder(orderId);
    }
}
