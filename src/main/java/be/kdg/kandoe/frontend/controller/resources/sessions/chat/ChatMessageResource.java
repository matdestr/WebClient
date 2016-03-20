package be.kdg.kandoe.frontend.controller.resources.sessions.chat;

import be.kdg.kandoe.frontend.controller.resources.users.UserResource;
import lombok.Data;

import java.util.Date;

/**
 * Resource for the {@link be.kdg.kandoe.backend.model.sessions.ChatMessage} model
 */

@Data
public class ChatMessageResource {
    private UserResource user;
    private String content;
    private Date dateTime;
}
