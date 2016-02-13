package unittest;

import be.kdg.kandoe.backend.config.BackendContextConfig;
import be.kdg.kandoe.backend.model.oauth.OAuthClientDetails;
import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.OAuthClientDetailsService;
import be.kdg.kandoe.backend.service.exceptions.OAuthClientDetailsServiceException;
import be.kdg.kandoe.backend.service.exceptions.OrganizationServiceException;
import be.kdg.kandoe.backend.service.exceptions.UserServiceException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {BackendContextConfig.class })
@Transactional
@Rollback
public class TestOAuthClientDetailsService {
    @Autowired
    private OAuthClientDetailsService oAuthClientDetailsService;

    @Test
    public void addNewOAuthClient(){
        OAuthClientDetails clientDetails = new OAuthClientDetails("test");
        clientDetails.setAuthorizedGrandTypes("password", "refresh_token");
        clientDetails.setAuthorities("ROLE_TEST_CLIENT");
        clientDetails.setScopes("read");
        clientDetails.setSecret("secret");

        oAuthClientDetailsService.addClientsDetails(clientDetails);
    }

    @Test(expected = OAuthClientDetailsServiceException.class)
    public void addExistingOAuthClient(){
        OAuthClientDetails clientDetails1 = new OAuthClientDetails("test");
        clientDetails1.setAuthorizedGrandTypes("password", "refresh_token");
        clientDetails1.setAuthorities("ROLE_TEST_CLIENT");
        clientDetails1.setScopes("read");
        clientDetails1.setSecret("secret");

        OAuthClientDetails clientDetails2 = new OAuthClientDetails("test");
        clientDetails1.setAuthorizedGrandTypes("password", "refresh_token");
        clientDetails1.setAuthorities("ROLE_TEST_CLIENT");
        clientDetails1.setScopes("read");
        clientDetails1.setSecret("secret");

        oAuthClientDetailsService.addClientsDetails(clientDetails1);
        oAuthClientDetailsService.addClientsDetails(clientDetails2);
    }

    @Test
    public void loadExistingClientDetailsByClientId() throws UserServiceException {
        OAuthClientDetails clientDetails = new OAuthClientDetails("test");
        clientDetails.setAuthorizedGrandTypes("password", "refresh_token");
        clientDetails.setAuthorities("ROLE_TEST_CLIENT");
        clientDetails.setScopes("read");
        clientDetails.setSecret("secret");

        oAuthClientDetailsService.addClientsDetails(clientDetails);

        ClientDetails existing = oAuthClientDetailsService.loadClientByClientId(clientDetails.getClientId());
        assertEquals(clientDetails.getClientId(), existing.getClientId());
    }

    @Test(expected = ClientRegistrationException.class)
    public void loadUnexistingClientDetailsByName() {
        ClientDetails existing = oAuthClientDetailsService.loadClientByClientId("unexisting-client-id");
        assertNull(existing);
    }
}
