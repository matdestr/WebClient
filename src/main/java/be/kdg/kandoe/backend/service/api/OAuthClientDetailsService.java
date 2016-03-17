/**
 * Interface to manage authorization for clientdetails
 */
package be.kdg.kandoe.backend.service.api;

import be.kdg.kandoe.backend.model.oauth.OAuthClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;

public interface OAuthClientDetailsService extends ClientDetailsService {
    OAuthClientDetails addClientsDetails(OAuthClientDetails oAuthClientDetails);
}
