package be.kdg.kandoe.frontend.controller.resources.helpers;

import be.kdg.kandoe.backend.service.exceptions.CanDoServiceRuntimeException;
import be.kdg.kandoe.frontend.controller.resources.errors.ErrorResource;
import be.kdg.kandoe.frontend.controller.resources.errors.ValidationErrorResource;
import be.kdg.kandoe.frontend.controller.rest.exceptions.CanDoControllerRuntimeException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.messaging.support.ErrorMessage;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@ControllerAdvice
public class ErrorHandler /*extends ResponseEntityExceptionHandler*/ {
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
    
    //@ExceptionHandler(RuntimeException.class)
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

    /*@Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Throwable mostSpecificCause = ex.getMostSpecificCause();
        ErrorMessage errorMessage;
        if (mostSpecificCause != null) {
            String exceptionName = mostSpecificCause.getClass().getName();
            String message = mostSpecificCause.getMessage();
            errorMessage = new ErrorMessage(mostSpecificCause);
        } else {
            errorMessage = new ErrorMessage(ex);
        }
        return new ResponseEntity(errorMessage, headers, status);
    }*/
}
