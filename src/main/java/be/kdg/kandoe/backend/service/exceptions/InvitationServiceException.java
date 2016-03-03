package be.kdg.kandoe.backend.service.exceptions;

public class InvitationServiceException extends RuntimeException {

    public InvitationServiceException(String message) {
        super(message);
    }

    public InvitationServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
