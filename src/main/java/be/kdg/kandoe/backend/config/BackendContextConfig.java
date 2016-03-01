package be.kdg.kandoe.backend.config;

import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = "be.kdg.kandoe.backend",
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION,
                value = Configuration.class)}
)
@EnableJpaRepositories(basePackages = "be.kdg.kandoe.backend.persistence")
@Import({be.kdg.kandoe.backend.config.DataSourceConfig.class, be.kdg.kandoe.backend.config.EntityTransactionManagerConfig.class})
public class BackendContextConfig {
    @Bean
    static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer(){
        return new PropertySourcesPlaceholderConfigurer();
    }
}
