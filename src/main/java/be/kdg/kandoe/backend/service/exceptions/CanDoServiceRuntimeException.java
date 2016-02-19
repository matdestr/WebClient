package be.kdg.kandoe.backend.service.exceptions;

public class CanDoServiceRuntimeException extends RuntimeException {
    public CanDoServiceRuntimeException(String message) {
        super(message);
    }

    public CanDoServiceRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
