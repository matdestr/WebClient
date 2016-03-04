package be.kdg.kandoe.frontend.config.security.resolvers;

import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.UserService;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * This class enables the usage of @AuthenticationPrincipal for an OAuth2Authentication object.
 * https://stackoverflow.com/questions/12464042/spring-oauth-oauth2-how-can-i-get-the-client-credentials-in-a-spring-mvc-cont
 * */
public class OAuth2UserArgumentResolver implements HandlerMethodArgumentResolver, org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver {
    private final UserService userService;
    
    public OAuth2UserArgumentResolver(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return (parameter.getParameterAnnotation(AuthenticationPrincipal.class) != null
                && parameter.getParameterType().equals(User.class));
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, Message<?> message) throws Exception {
        return getUserFromAuthentication();
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, 
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        return getUserFromAuthentication();
    }

    private User getUserFromAuthentication(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null)
            return null;

        User user = null;

        if (authentication.getClass().isAssignableFrom(OAuth2Authentication.class))
            user = userService.getUserByUsername(authentication.getPrincipal().toString());

        return user;
    }
}
