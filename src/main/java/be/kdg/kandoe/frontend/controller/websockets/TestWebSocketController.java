package be.kdg.kandoe.frontend.controller.websockets;

import be.kdg.kandoe.backend.model.users.User;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class TestWebSocketController {

    @PreAuthorize("isAuthenticated()")
    @MessageMapping("/test")
    @SendTo("/topic/values")
    public String test(@AuthenticationPrincipal User user) throws Exception {
        return "value";
    }
}
