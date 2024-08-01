package com.github.rusichpt.feedback.controller.payload;

public record ProductReviewResponse(Integer productId, Integer rating, String review, String userId) {
}
