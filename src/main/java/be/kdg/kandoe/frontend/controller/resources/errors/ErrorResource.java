package be.kdg.kandoe.frontend.controller.resources.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Resource to expose errors over JSON
 */

@AllArgsConstructor
public class ErrorResource
{
    @Getter
    private final String message;
}
