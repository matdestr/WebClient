package be.kdg.kandoe.frontend.controller.resources.properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Properties for facebook authentication
 */

@Component
@lombok.Value
public class FacebookProperties {
    private final String appId;
    private final String secret;

    @Autowired
    public FacebookProperties(@Value("${facebook.app.secret}") String secret,
                              @Value("${facebook.app.id}") String appId){

        this.appId = appId;
        this.secret = secret;
    }
}
