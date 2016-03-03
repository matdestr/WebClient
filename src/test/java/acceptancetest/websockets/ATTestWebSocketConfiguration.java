package acceptancetest.websockets;

import be.kdg.kandoe.frontend.config.RootContextConfig;
import be.kdg.kandoe.frontend.config.WebContextConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class ATTestWebSocketConfiguration {
    private WebSocketStompClient stompClient;
    private String baseUrl;

    @Before
    public void setUp(){
        baseUrl = String.format("ws://%s/ws",System.getProperty("test.baseUrl") );

        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        WebSocketClient transport = new SockJsClient(transports);
        stompClient = new WebSocketStompClient(transport);
        stompClient.setMessageConverter(new StringMessageConverter());
    }

    @Test
    public void testWebsocketsWithStomp() throws InterruptedException {
        String url = "ws://localhost:8000/kandoe/ws";

        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Throwable> failure = new AtomicReference<>();

        StompSessionHandler handler = new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                System.out.println("connected");
                session.subscribe("/topic/values", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders stompHeaders) {
                        return String.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders stompHeaders, Object o) {
                        try {
                            System.out.println(o.toString());
                            assertNotNull(o);
                            assertEquals(o, "value");
                        } catch (Throwable t) {
                            failure.set(t);
                        } finally {
                            session.disconnect();
                            latch.countDown();
                        }
                    }
                });
                session.send("/test", "test");
            }

        };

        stompClient.connect(url, handler);
/*
        if (failure.get() != null) {
            throw new AssertionError("", failure.get());
        }

        if (!latch.await(10, TimeUnit.SECONDS)) {
            fail("no values returned");
        }
        */
    }


}
