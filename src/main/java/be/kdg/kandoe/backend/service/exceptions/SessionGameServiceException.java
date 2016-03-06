package be.kdg.kandoe.backend.service.exceptions;

public class SessionGameServiceException extends CanDoServiceRuntimeException {
    public SessionGameServiceException(String message) {
        super(message);
    }

    public SessionGameServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
