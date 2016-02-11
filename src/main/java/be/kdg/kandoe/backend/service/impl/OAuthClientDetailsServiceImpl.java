package be.kdg.kandoe.backend.service.impl;

import be.kdg.kandoe.backend.persistence.api.OAuthClientDetailsRepository;
import be.kdg.kandoe.backend.service.api.OAuthClientDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;

@Service
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
}
