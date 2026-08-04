package com.example.inventory_service.controller;

import com.example.inventory_service.dto.ReviewRequest;
import com.example.inventory_service.dto.ReviewResponse;
import com.example.inventory_service.security.CurrentUser;
import com.example.inventory_service.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/products/{productId}/reviews")
    public ResponseEntity<ReviewResponse> addReview(@PathVariable Long productId,
                                                    @Valid @RequestBody ReviewRequest request) {
        Long userId = CurrentUser.id();
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.addReview(productId, userId, request));
    }

    @GetMapping("/products/{productId}/reviews")
    public List<ReviewResponse> getReviews(@PathVariable Long productId) {
        return reviewService.getReviews(productId);
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        Long userId = CurrentUser.id();
        Boolean isAdmin = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication())
                .getAuthorities().stream().anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ADMIN"));
        reviewService.deleteReview(reviewId, userId, isAdmin);
        return ResponseEntity.noContent().build();
    }
}
