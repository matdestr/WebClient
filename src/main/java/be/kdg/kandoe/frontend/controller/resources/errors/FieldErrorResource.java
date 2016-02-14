package be.kdg.kandoe.frontend.controller.resources.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class FieldErrorResource
{
    @Getter
    private final String field;
    @Getter
    private final String message;
}

