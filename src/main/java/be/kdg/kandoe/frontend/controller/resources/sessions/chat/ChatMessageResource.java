package be.kdg.kandoe.frontend.controller.resources.sessions.chat;

import be.kdg.kandoe.backend.model.users.User;
import lombok.Data;

import java.util.Date;

@Data
public class ChatMessageResource {
    private User user;
    private String message;
    private Date date;
}
