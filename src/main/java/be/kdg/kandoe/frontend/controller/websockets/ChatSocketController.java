package be.kdg.kandoe.frontend.controller.websockets;

import be.kdg.kandoe.backend.model.sessions.ChatMessage;
import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.SessionService;
import be.kdg.kandoe.frontend.controller.resources.sessions.chat.ChatMessageResource;
import be.kdg.kandoe.frontend.controller.resources.sessions.chat.CreateChatMessageResource;
import be.kdg.kandoe.frontend.controller.resources.users.UserResource;
import be.kdg.kandoe.frontend.controller.rest.exceptions.CanDoControllerRuntimeException;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Websocket controller for ChatMessages
 */

@Controller
public class ChatSocketController {
    private SessionService sessionService;
    private MapperFacade mapperFacade;

    @Autowired
    public ChatSocketController(SessionService sessionService, MapperFacade mapperFacade) {
        this.sessionService = sessionService;
        this.mapperFacade = mapperFacade;
    }
    @PreAuthorize("isAuthenticated()")
    @MessageMapping("/sessions/{sessionId}/messages")
    @SendTo("/topic/sessions/{sessionId}/messages")
    public ChatMessageResource onReceiveMessage(@AuthenticationPrincipal User user,
                                                @DestinationVariable("sessionId") int sessionId,
                                                CreateChatMessageResource message){


        Session session = sessionService.getSessionById(sessionId);

        checkUserIsParticipant(user, session);

        Date dateCreated = new Date();

        ChatMessage chatMessage = new ChatMessage();
        ChatMessageResource resource = new ChatMessageResource();
        
        chatMessage.setUser(user);
        chatMessage.setSession(session);
        chatMessage.setContent(message.getMessage());
        chatMessage.setDateTime(dateCreated.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        
        session.getChatMessages().add(chatMessage);
        session = sessionService.updateSession(session);

        resource.setContent(message.getMessage());
        resource.setUser(mapperFacade.map(user, UserResource.class));
        resource.setDateTime(dateCreated);
        
        return resource;
    }

    private void checkUserIsParticipant(User user, Session session) {
        if (session == null)
            throw new CanDoControllerRuntimeException("Invalid session ID");

        if (user == null)
            throw new CanDoControllerRuntimeException("Invalid user ID");

        if (!session.isUserParticipant(user.getUserId()))
            throw new CanDoControllerRuntimeException("You must be a participant of the session to perform this action", HttpStatus.FORBIDDEN);
    }
}
