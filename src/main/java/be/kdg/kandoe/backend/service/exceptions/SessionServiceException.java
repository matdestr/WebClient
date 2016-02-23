package be.kdg.kandoe.backend.service.exceptions;


public class SessionServiceException extends CanDoServiceRuntimeException {

    public SessionServiceException(String message) {
        super(message);
    }

    public SessionServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
