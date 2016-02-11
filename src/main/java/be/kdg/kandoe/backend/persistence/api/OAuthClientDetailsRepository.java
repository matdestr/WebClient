package be.kdg.kandoe.backend.persistence.api;

import be.kdg.kandoe.backend.model.oauth.OAuthClientDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OAuthClientDetailsRepository extends JpaRepository<OAuthClientDetails, Integer> {
    OAuthClientDetails findOAuthClientDetailsByClientId(String clientId);
}
