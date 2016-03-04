package be.kdg.kandoe.frontend.config;

import be.kdg.kandoe.frontend.config.security.resolvers.OAuth2UserArgumentResolver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.List;
import java.util.Locale;

@Configuration
@EnableWebMvc
@EnableGlobalMethodSecurity(prePostEnabled = true)
@PropertySource("classpath:application.properties")
@ComponentScan(basePackages = "be.kdg.kandoe.frontend", excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, value = Configuration.class)})
@Import( WebSocketConfig.class)
public class WebContextConfig extends WebMvcConfigurerAdapter {
    @Autowired
    private OAuth2UserArgumentResolver oAuth2UserArgumentResolver;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/css/**")
                .addResourceLocations("/resources/css/");

        registry.addResourceHandler("/html/**")
                .addResourceLocations("/resources/html/");

        registry.addResourceHandler("/fonts/**")
                .addResourceLocations("/resources/fonts/");

        registry.addResourceHandler("/img/**")
                .addResourceLocations("/resources/img/");

        registry.addResourceHandler("/js/**")
                .addResourceLocations("/resources/js/");
    }

    @Override
    public Validator getValidator() {
        // Create global validator bean for Hibernate Validation (JSR-303)
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        // pass MessageSource Bean so error messages have i18n support
        bean.setValidationMessageSource(messageSource());
        return bean;
    }

    @Bean
    public ReloadableResourceBundleMessageSource messageSource()
    {
        ReloadableResourceBundleMessageSource messageSource =
                new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:bundles/messages");
        return messageSource;
    }
    
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(new Locale("en", "US"));
        
        return localeResolver;
    }

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setDefaultEncoding("utf-8");
        resolver.setMaxInMemorySize(200000);
        resolver.setMaxUploadSize(100000);
        return resolver;
    }

    @Bean
    static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer(){
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(oAuth2UserArgumentResolver);
    }

}
