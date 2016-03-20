package be.kdg.kandoe.frontend.controller.resources.helpers;

import be.kdg.kandoe.backend.service.exceptions.CanDoServiceRuntimeException;
import be.kdg.kandoe.frontend.controller.resources.errors.ErrorResource;
import be.kdg.kandoe.frontend.controller.resources.errors.ValidationErrorResource;
import be.kdg.kandoe.frontend.controller.rest.exceptions.CanDoControllerRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.List;

/**
 * Class that handles all the errors coming from Controllers
 */

@ControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResource> processValidationError(MethodArgumentNotValidException ex)
    {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();

        return new ResponseEntity<>(processFieldErrors(fieldErrors), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    private ValidationErrorResource processFieldErrors(List<FieldError> fieldErrors) {
        ValidationErrorResource validationErrorResource = new ValidationErrorResource();
        fieldErrors.forEach(f -> validationErrorResource.addFieldError(f.getField(), f.getDefaultMessage()));
        return validationErrorResource;
    }

    @ExceptionHandler(CanDoServiceRuntimeException.class)
    public ResponseEntity<ErrorResource> processRuntimeException(RuntimeException e) {
        ErrorResource errorResource = new ErrorResource(e.getLocalizedMessage());
        return new ResponseEntity<ErrorResource>(errorResource, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(CanDoControllerRuntimeException.class)
    public ResponseEntity<ErrorResource> processControllerException(CanDoControllerRuntimeException e) {
        ErrorResource errorResource = new ErrorResource(e.getLocalizedMessage());
        return new ResponseEntity<ErrorResource>(
                errorResource,
                e.getHttpStatus() == null ? HttpStatus.BAD_REQUEST : e.getHttpStatus()
        );
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResource> handleMaxSizeExceededException() {
        ErrorResource errorResource = new ErrorResource("Image is too large (max size: 100kb)");
        return new ResponseEntity<ErrorResource>(errorResource, HttpStatus.BAD_REQUEST);
    }
}
