package be.kdg.kandoe.frontend.controller.websockets;

import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.frontend.controller.resources.sessions.CardPositionResource;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SessionGameWebSocketController {
    private MapperFacade mapperFacade;

    @Autowired
    public SessionGameWebSocketController(MapperFacade mapperFacade) {
        this.mapperFacade = mapperFacade;
    }
    
    /*@PreAuthorize("isAuthenticated()")
    @MessageMapping("/sessions/{sessionId}/positions")
    @SendTo("/topic/sessions/{sessionId}/positions")
    public CardPositionResource onReceiveMessageUpvoteCard(@AuthenticationPrincipal User user,
                                                           @DestinationVariable("sessionId") int sessionId,
                                                           @RequestParam("cardDetailsId"))*/
}
