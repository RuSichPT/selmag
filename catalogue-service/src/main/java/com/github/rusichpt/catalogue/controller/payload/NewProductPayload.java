package com.github.rusichpt.catalogue.controller.payload;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NewProductPayload(
        @NotNull(message = "{catalogue.errors.product.create.errors.title_is_null}")
        @Size(min = 3,max = 50, message = "{catalogue.errors.product.create.errors.title_size_is_invalid}") //{}-позволяют автоматически подставить локализованное сообщение
        String title,
        @Size(max = 1000, message = "{catalogue.errors.product.create.errors.details_size_are_invalid}")
        String details) {
}
