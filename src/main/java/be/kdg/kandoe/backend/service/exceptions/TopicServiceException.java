package be.kdg.kandoe.backend.service.exceptions;

/**
 * Created by thaneestevens on 20/02/16.
 */
public class TopicServiceException extends CanDoServiceRuntimeException {
    public TopicServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public TopicServiceException(String message) {
        super(message);
    }
}