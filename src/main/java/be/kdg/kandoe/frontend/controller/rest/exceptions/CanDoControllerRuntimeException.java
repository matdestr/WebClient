package be.kdg.kandoe.frontend.controller.rest.exceptions;

import org.springframework.http.HttpStatus;

/**
 * General controller exception
 */
public class CanDoControllerRuntimeException extends RuntimeException {
    private HttpStatus httpStatus;

    public CanDoControllerRuntimeException(String message) {
        super(message);
    }

    public CanDoControllerRuntimeException(String message, HttpStatus httpStatus) {
        this(message);
        this.httpStatus = httpStatus;
    }

    public CanDoControllerRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public CanDoControllerRuntimeException(String message, Throwable cause, HttpStatus httpStatus) {
        this(message, cause);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
