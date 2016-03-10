package be.kdg.kandoe.backend.service.exceptions;

public class CategoryServiceException extends CanDoServiceRuntimeException {
    public CategoryServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public CategoryServiceException(String message) {
        super(message);
    }
}
