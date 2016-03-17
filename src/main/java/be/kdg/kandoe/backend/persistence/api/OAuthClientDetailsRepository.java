package be.kdg.kandoe.backend.persistence.api;

import be.kdg.kandoe.backend.model.oauth.OAuthClientDetails;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Proxy interface for the CRUD Repository for the {@link OAuthClientDetails} model
 */

public interface OAuthClientDetailsRepository extends JpaRepository<OAuthClientDetails, Integer> {
    OAuthClientDetails findOAuthClientDetailsByClientId(String clientId);
}
