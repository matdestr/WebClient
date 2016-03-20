package be.kdg.kandoe.backend.service.properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Contains the needed properties to send e-mails from within the application.
 * */
@PropertySource("classpath:application.properties")
@Component
@lombok.Value
public class MailProperties {
    private final String host;
    private final int port;
    private final String username;
    private final String password;

    @Autowired
    public MailProperties(@Value("${mail.gmail.host}") String host,
                          @Value("${mail.gmail.port}") int port,
                          @Value("${mail.gmail.email}") String username,
                          @Value("${mail.gmail.password}") String password){
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }
}
