package be.kdg.kandoe.backend.service.api;

import be.kdg.kandoe.backend.model.oauth.OAuthClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;

/**
 * Interface for management of OAuth client details.
 */
public interface OAuthClientDetailsService extends ClientDetailsService {
    /**
     * Saves the OAuth client details of an application for authentication and authorization.
     * */
    OAuthClientDetails addClientsDetails(OAuthClientDetails oAuthClientDetails);
}
