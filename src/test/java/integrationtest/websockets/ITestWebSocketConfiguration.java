package integrationtest.websockets;

import be.kdg.kandoe.frontend.config.RootContextConfig;
import be.kdg.kandoe.frontend.config.WebContextConfig;
import integrationtest.oauth.OAuth2TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootContextConfig.class, WebContextConfig.class, WebSocketTestConfig.class})
@WebAppConfiguration
public class ITestWebSocketConfiguration {
    private WebSocketClient webSocketClient;
    private WebSocketStompClient stompClient;
    private String baseUrl;

    @Before
    public void setUp(){
        baseUrl = String.format("ws://%s/",System.getProperty("test.baseUrl") );

        webSocketClient = new StandardWebSocketClient();
        stompClient = new WebSocketStompClient(webSocketClient);
        stompClient.setMessageConverter(new StringMessageConverter());
    }

    @Test
    public void testWebsocketsWithStomp(){
        String url = baseUrl + "test";
        StompSessionHandler handler = new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                System.out.println("connected");
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                System.out.println(payload.toString());
                super.handleFrame(headers, payload);
            }
        };
        stompClient.connect(url, handler);
    }
}
