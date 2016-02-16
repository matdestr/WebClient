package be.kdg.kandoe.frontend.controller.rest.social;

import be.kdg.kandoe.frontend.controller.resources.properties.FacebookProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by Wannes on 16/02/16.
 */
@RestController()
@RequestMapping(value = "/api/social/facebook")
public class FacebookController {
    @Autowired
    private FacebookProperties properties;

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
        System.out.println(grant.getScope());
        Connection<Facebook> facebookConnection = connectionFactory.createConnection(grant);

        System.out.println("Received access to facebook profile of " + facebookConnection.getDisplayName());
        System.out.println(facebookConnection.fetchUserProfile().getEmail());

        response.sendRedirect("http://localhost:8080/kandoe");
        //todo send retrieved data to register page if no email address is received
    }
}
