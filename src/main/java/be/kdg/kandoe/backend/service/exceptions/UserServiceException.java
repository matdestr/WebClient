package be.kdg.kandoe.backend.service.exceptions;

public class UserServiceException extends CanDoServiceRuntimeException {
    public UserServiceException(String message) {
        super(message);
    }

    public UserServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
