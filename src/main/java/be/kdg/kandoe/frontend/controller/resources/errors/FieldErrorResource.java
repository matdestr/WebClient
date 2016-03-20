package be.kdg.kandoe.frontend.controller.resources.errors;

import lombok.Getter;

/**
 * Error resource for FieldErrors
 */

public class FieldErrorResource extends ErrorResource
{
    @Getter
    private final String field;

    public FieldErrorResource(String field, String message) {
        super(message);
        this.field = field;
    }
}

