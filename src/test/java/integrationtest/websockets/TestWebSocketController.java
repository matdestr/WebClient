package integrationtest.websockets;

import be.kdg.kandoe.frontend.controller.websockets.WebSocketController;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@MessageMapping("/")
public class TestWebSocketController implements WebSocketController {
    @Override
    public String[] getEndpoints() {
        return new String[]{"/test"};
    }

    @MessageMapping("/hello")
    public String test(String name) throws Exception {
        Thread.sleep(3000); // simulated delay
        return "Hello, " + name + "!";
    }

}
