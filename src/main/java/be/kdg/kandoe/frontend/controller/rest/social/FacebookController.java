package be.kdg.kandoe.frontend.controller.rest.social;

import be.kdg.kandoe.backend.service.api.UserService;
import be.kdg.kandoe.frontend.controller.resources.properties.FacebookProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@RestController()
@RequestMapping(value = "/api/social/facebook")
public class FacebookController {
    @Autowired
    private FacebookProperties properties;

    @Autowired
    private UserService userService;

    private FacebookConnectionFactory connectionFactory;

    public FacebookController(){
    }

    @PostConstruct
    public void initialize(){
        this.connectionFactory = new FacebookConnectionFactory(properties.getAppId(), properties.getSecret());
    }

    private String getAuthorizeUrl(){
        OAuth2Operations operations = connectionFactory.getOAuthOperations();
        OAuth2Parameters parameters = new OAuth2Parameters();
        parameters.setRedirectUri("http://localhost:8080/kandoe/api/social/facebook/redirect");
        parameters.setScope("public_profile,email");

        return operations.buildAuthorizeUrl(parameters);
    }

    @RequestMapping(value = "/request", method = RequestMethod.GET)
    public void requestAccess(HttpServletResponse response) throws IOException {
        String authUrl = getAuthorizeUrl();
        response.sendRedirect(authUrl);
    }

    @RequestMapping(value = "/redirect", method = RequestMethod.GET)
    public void onRedirectFromFacebook(@RequestParam("code") String authorizationCode, HttpServletResponse response) throws IOException {
        OAuth2Operations oauthOperations = connectionFactory.getOAuthOperations();

        AccessGrant grant = oauthOperations.exchangeForAccess(authorizationCode, "http://localhost:8080/kandoe/api/social/facebook/redirect", null);
        Connection<Facebook> facebookConnection = connectionFactory.createConnection(grant);

        String name = facebookConnection.getDisplayName();
        String email = facebookConnection.fetchUserProfile().getEmail();

        if (email == null || email.isEmpty()){
            //todo redirect to email page and register

        } else {
            //todo register
            response.sendRedirect("http://localhost:8080/kandoe");
        }
    }
}
