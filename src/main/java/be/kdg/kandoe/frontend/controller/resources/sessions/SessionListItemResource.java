package be.kdg.kandoe.frontend.controller.resources.sessions;

import be.kdg.kandoe.backend.model.sessions.SessionStatus;
import be.kdg.kandoe.frontend.controller.resources.users.UserResource;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Resource for the {@link SessionListItemResource}
 */

@Data
public class SessionListItemResource {
    private int sessionId;
    private String organizationTitle;
    private String categoryTitle;
    private String topicTitle;
    private int participantAmount;
    private SessionStatus sessionStatus;
}
