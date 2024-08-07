package com.github.rusichpt.customerapp.client.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)// Переопределение с вызовом родительских
@ToString(callSuper = true)
public class ClientBadRequestException extends RuntimeException {

    private final List<String> errors;

    public ClientBadRequestException(String message, Throwable cause, List<String> errors) {
        super(message, cause);
        this.errors = errors;
    }
}
