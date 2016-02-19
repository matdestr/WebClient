package be.kdg.kandoe.backend.service.exceptions;

/**
 * Created on 19/02/2016
 *
 * @author Arne De Cock
 */
public class CategoryServiceException extends CanDoServiceRuntimeException {
    public CategoryServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public CategoryServiceException(String message) {
        super(message);
    }
}
