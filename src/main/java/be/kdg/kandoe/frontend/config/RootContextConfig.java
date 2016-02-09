package be.kdg.kandoe.frontend.config;

import be.kdg.kandoe.backend.config.BackendContextConfig;
import be.kdg.kandoe.frontend.config.security.SpringSecurityConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Configuration class that imports the {@link BackendContextConfig BackendContext Configuration class}.
 */
@Configuration
@Import({BackendContextConfig.class, SpringSecurityConfig.class})
public class RootContextConfig {
}
