package com.example.shop_service.repository;

import com.example.shop_service.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserIdAndStatus(Long userId, Cart.Status status);
}
