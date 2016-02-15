package be.kdg.kandoe.frontend.controller.resources.helpers;

import be.kdg.kandoe.backend.service.exceptions.KandoeServiceRuntimeException;
import be.kdg.kandoe.frontend.controller.resources.errors.ErrorResource;
import be.kdg.kandoe.frontend.controller.resources.errors.ValidationErrorResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResource> processValidationError(MethodArgumentNotValidException ex)
    {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();

        return new ResponseEntity<>(processFieldErrors(fieldErrors), HttpStatus.BAD_REQUEST);
    }

    private ValidationErrorResource processFieldErrors(List<FieldError> fieldErrors)
    {
        ValidationErrorResource validationErrorResource = new ValidationErrorResource();
        fieldErrors.forEach(f -> validationErrorResource.addFieldError(f.getField(), f.getDefaultMessage()));
        return validationErrorResource;
    }
    
    //@ExceptionHandler(RuntimeException.class)
    @ExceptionHandler(KandoeServiceRuntimeException.class)
    public ResponseEntity<ErrorResource> processRuntimeException(RuntimeException e) {
        ErrorResource errorResource = new ErrorResource(e.getLocalizedMessage());
        return new ResponseEntity<ErrorResource>(errorResource, HttpStatus.BAD_REQUEST);
    }
}
