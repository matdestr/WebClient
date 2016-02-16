package be.kdg.kandoe.frontend.controller.resources.properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by Wannes on 16/02/16.
 */
@Component
public class FacebookProperties {
    private final String appId;
    private final String secret;

    @Autowired
    public FacebookProperties(@Value("${facebook.app.secret}") String secret,
                              @Value("${facebook.app.id}") String appId){

        this.appId = appId;
        this.secret = secret;
    }

    public String getAppId() {
        return appId;
    }

    public String getSecret() {
        return secret;
    }
}
