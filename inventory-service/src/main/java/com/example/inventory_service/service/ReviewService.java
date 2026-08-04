package com.example.inventory_service.service;

import com.example.inventory_service.dto.ReviewRequest;
import com.example.inventory_service.dto.ReviewResponse;
import com.example.inventory_service.entity.Product;
import com.example.inventory_service.entity.Review;
import com.example.inventory_service.exception.ProductNotFoundException;
import com.example.inventory_service.repository.ProductRepository;
import com.example.inventory_service.repository.ReviewRepository;
import jakarta.security.auth.message.AuthException;
import org.apache.http.MessageConstraintException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.ErrorResponseException;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    public ReviewService(ReviewRepository reviewRepository, ProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
    }

    private ReviewResponse toResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .productId(review.getProduct().getId())
                .reviewerName(review.getReviewerName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }

    @Transactional
    public ReviewResponse addReview(Long productId, Long userId, ReviewRequest request){
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));

        Review review = Review.builder()
                .product(product)
                .userId(userId)
                .reviewerName(request.getReviewerName())
                .rating(request.getRating())
                .comment(request.getComment())
                .build();
        review = reviewRepository.save(review);

        return toResponse(review);
    }

    public List<ReviewResponse> getReviews(Long productId){
        return reviewRepository.findByProductId(productId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void deleteReview(Long reviewId, Long requestingUserId, Boolean isAdmin) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ProductNotFoundException("Review not found: " + reviewId));

        if(!isAdmin && !review.getUserId().equals(requestingUserId)){
            throw new RuntimeException("You can only delete your own review");
        }

        reviewRepository.delete(review);
    }
}
