package be.kdg.kandoe.frontend.controller.resources.sessions.chat;

import lombok.Data;

/**
 * Resource for the creation of a {@link be.kdg.kandoe.backend.model.sessions.ChatMessage}
 */

@Data
public class CreateChatMessageResource {
    private String message;
}
