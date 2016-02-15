package be.kdg.kandoe.backend.service.exceptions;

public class KandoeServiceRuntimeException extends RuntimeException {
    public KandoeServiceRuntimeException(String message) {
        super(message);
    }

    public KandoeServiceRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
