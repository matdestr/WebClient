package be.kdg.kandoe.frontend.controller.resources.errors;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ValidationErrorResource
{
    @Getter
    private final List<FieldErrorResource> fieldErrors = new ArrayList<>();

    public void addFieldError(String path, String message)
    {
        FieldErrorResource error = new FieldErrorResource(path, message);
        fieldErrors.add(error);
    }
}