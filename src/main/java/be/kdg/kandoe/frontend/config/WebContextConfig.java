package be.kdg.kandoe.frontend.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "be.kdg.kandoe.frontend", excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, value = Configuration.class)})
public class WebContextConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {

        registry.addResourceHandler("/css/**")
            .addResourceLocations("/resources/css/");

        registry.addResourceHandler("/fonts/**")
                .addResourceLocations("/resources/fonts/");

        /*
        registry.addResourceHandler("/img/**")
                .addResourceLocations("/resources/img/");
                */
        registry.addResourceHandler("/js/**")
                .addResourceLocations("/resources/js/");
        /*
        registry.addResourceHandler("/app/**").addResourceLocations("/app/");
        */

    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer)
    {
        configurer.enable();
    }
}
