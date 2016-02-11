package be.kdg.kandoe.backend.service.exceptions;

/**
 * Created by Wannes on 2/11/2016.
 */
public class OrganizationServiceException extends RuntimeException {

    public OrganizationServiceException(String message) {
        super(message);
    }

    public OrganizationServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
