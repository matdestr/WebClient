/**
 * Interface to manage authorization for clientdetails
 */
package be.kdg.kandoe.backend.service.api;

import be.kdg.kandoe.backend.model.oauth.OAuthClientDetails;
import be.kdg.kandoe.backend.model.users.Invitation;
import org.springframework.security.oauth2.provider.ClientDetailsService;

/**
 * Interface that extends the ClientDetailsService used the frontend for allowed clients
 */

public interface OAuthClientDetailsService extends ClientDetailsService {
    OAuthClientDetails addClientsDetails(OAuthClientDetails oAuthClientDetails);
}
