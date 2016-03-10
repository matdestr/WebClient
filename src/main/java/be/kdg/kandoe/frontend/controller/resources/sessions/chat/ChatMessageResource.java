package be.kdg.kandoe.frontend.controller.resources.sessions.chat;

import be.kdg.kandoe.frontend.controller.resources.users.UserResource;
import lombok.Data;

import java.util.Date;

@Data
public class ChatMessageResource {
    private UserResource user;
    private String message;
    private Date date;
}
