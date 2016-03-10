package be.kdg.kandoe.backend.service.exceptions;

public class TopicServiceException extends CanDoServiceRuntimeException {
    public TopicServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public TopicServiceException(String message) {
        super(message);
    }
}