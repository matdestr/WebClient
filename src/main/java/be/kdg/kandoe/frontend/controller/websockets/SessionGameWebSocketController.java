package be.kdg.kandoe.frontend.controller.websockets;

import be.kdg.kandoe.backend.service.api.SessionGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller
@PreAuthorize("isAuthenticated()")
public class SessionGameWebSocketController {
    private SessionGameService sessionGameService;

    @Autowired
    public SessionGameWebSocketController(SessionGameService sessionGameService) {
        this.sessionGameService = sessionGameService;
    }
}
