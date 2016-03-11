package be.kdg.kandoe.frontend.controller.websockets;

import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.frontend.controller.resources.sessions.chat.ChatMessageResource;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Controller
public class TestWebSocketController {

    @MessageMapping("/test")
    @SendTo("/topic/values")
    public ChatMessageResource test(@AuthenticationPrincipal User user) throws Exception {
        System.out.println("test");
        return new ChatMessageResource();
    }
}
