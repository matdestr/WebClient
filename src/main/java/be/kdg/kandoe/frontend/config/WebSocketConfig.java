package be.kdg.kandoe.frontend.config;

import be.kdg.kandoe.frontend.controller.websockets.WebSocketController;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer implements ApplicationContextAware{

    private ApplicationContext applicationContext;
    private StompEndpointRegistry registry;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");

    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        this.registry = registry;
        init();
    }

    private void init() {
        List<WebSocketController> webSocketHandlers = new ArrayList<>(applicationContext.getBeansOfType(WebSocketController.class).values());
        webSocketHandlers.forEach(w -> registerWebSocket(w));
    }

    private void registerWebSocket(WebSocketController ws) {
        registry.addEndpoint(ws.getEndpoints()).withSockJS();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
