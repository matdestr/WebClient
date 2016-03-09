package be.kdg.kandoe.frontend.controller.websockets;

import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.frontend.controller.resources.sessions.chat.ChatMessageResource;
import be.kdg.kandoe.frontend.controller.resources.sessions.chat.CreateChatMessageResource;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.Date;

@Controller
public class ChatSocketController {
    @PreAuthorize("isAuthenticated()")
    @MessageMapping("/messages")
    @SendTo("/topic/session/messages")
    public ChatMessageResource onReceiveMessage(@AuthenticationPrincipal User user, CreateChatMessageResource message){
        ChatMessageResource resource = new ChatMessageResource();
        resource.setMessage(message.getMessage());
        resource.setUser(user);
        resource.setDate(new Date());
        return resource;
    }
}
