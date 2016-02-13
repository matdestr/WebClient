package be.kdg.kandoe.backend.service.exceptions;

public class OAuthClientDetailsServiceException extends RuntimeException {
    public OAuthClientDetailsServiceException() {
    }

    public OAuthClientDetailsServiceException(String message) {
        super(message);
    }

    public OAuthClientDetailsServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public OAuthClientDetailsServiceException(Throwable cause) {
        super(cause);
    }

    public OAuthClientDetailsServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
