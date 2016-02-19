package be.kdg.kandoe.backend.service.exceptions;

public class OrganizationServiceException extends CanDoServiceRuntimeException {

    public OrganizationServiceException(String message) {
        super(message);
    }

    public OrganizationServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
