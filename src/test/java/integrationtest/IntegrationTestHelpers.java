package integrationtest;

import be.kdg.kandoe.backend.model.oauth.OAuthClientDetails;

public class IntegrationTestHelpers {
    public static OAuthClientDetails getOAuthClientDetails() {
        OAuthClientDetails newClientDetails = new OAuthClientDetails("test");
        newClientDetails.setAuthorizedGrandTypes("password", "refresh_token");
        newClientDetails.setAuthorities("ROLE_TEST_CLIENT");
        newClientDetails.setScopes("read");
        newClientDetails.setSecret("secret");
        
        return newClientDetails;
    }
}
