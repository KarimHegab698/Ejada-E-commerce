package com.example.shop_service.controller;


import com.example.shop_service.dto.AddCartItemRequest;
import com.example.shop_service.dto.CartResponse;
import com.example.shop_service.dto.UpdateQuantityRequest;
import com.example.shop_service.security.CurrentUser;
import com.example.shop_service.service.CartService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public CartResponse getCart(){
        return cartService.getActiveCart(CurrentUser.id());
    }

    @PostMapping("/items")
    public CartResponse addItem(@Valid @RequestBody AddCartItemRequest request){
        return cartService.addItem(CurrentUser.id(), request);
    }

    @DeleteMapping("/items/{cartItemId}")
    public CartResponse removeItem(@PathVariable Long cartItemId){
        return cartService.removeItem(CurrentUser.id(), cartItemId);
    }

    @PatchMapping("/items/{cartItemId}")
    public CartResponse updateQuantity(@PathVariable Long cartItemId, @Valid @RequestBody UpdateQuantityRequest request) {
        return cartService.updateItemQuantity(CurrentUser.id(), cartItemId, request);
    }
}
