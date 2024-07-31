package com.github.rusichpt.customerapp.client.payload;

public record NewProductReviewPayloadClient(Integer productId, Integer rating, String review) {
}
