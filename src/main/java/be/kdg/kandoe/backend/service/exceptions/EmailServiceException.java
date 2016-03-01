package be.kdg.kandoe.backend.service.exceptions;

/**
 * Created by Wannes on 29/02/16.
 */
public class EmailServiceException extends RuntimeException {
    public EmailServiceException(String message) {
        super(message);
    }

    public EmailServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
