package com.github.rusichpt.customerapp.controller.payload;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NewProductReviewPayload(
        @NotNull(message = "{customer.products.reviews.create.errors.rating_is_null}")
        //{}-позволяют автоматически подставить локализованное сообщение
        @Min(value = 1, message = "{customer.products.reviews.create.errors.rating_is_below_min}")
        @Max(value = 5, message = "{customer.products.reviews.create.errors.rating_is_above_max}")
        Integer rating,
        @Size(max = 1000, message = "{customer.products.reviews.create.errors.review_is_too_big}")
        String review) {
}
