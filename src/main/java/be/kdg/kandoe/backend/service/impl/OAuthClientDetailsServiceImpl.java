package be.kdg.kandoe.backend.service.impl;

import be.kdg.kandoe.backend.model.oauth.OAuthClientDetails;
import be.kdg.kandoe.backend.persistence.api.OAuthClientDetailsRepository;
import be.kdg.kandoe.backend.service.api.OAuthClientDetailsService;
import be.kdg.kandoe.backend.service.exceptions.OAuthClientDetailsServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OAuthClientDetailsServiceImpl implements OAuthClientDetailsService {
    @Autowired
    private OAuthClientDetailsRepository oAuthClientDetailsRepository;

    @Override
    public ClientDetails loadClientByClientId(String s) throws ClientRegistrationException {
        ClientDetails clientDetails = oAuthClientDetailsRepository.findOAuthClientDetailsByClientId(s);

        if (clientDetails == null)
            throw new ClientRegistrationException(String.format("No client found with id (%s)", s));

        return clientDetails;
    }

    @Override
    public OAuthClientDetails addClientsDetails(OAuthClientDetails oAuthClientDetails) {
        try {
            return oAuthClientDetailsRepository.save(oAuthClientDetails);
        } catch (Exception e) {
            throw new OAuthClientDetailsServiceException(String.format("Could not save client with id (%s)", oAuthClientDetails.getClientId()));
        }
    }
}
