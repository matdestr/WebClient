package be.kdg.kandoe.backend.model.sessions;

import be.kdg.kandoe.backend.model.users.User;

import java.time.LocalDateTime;

public class ChatMessage {
    private User user;
    private String content;
    private LocalDateTime dateTime;
}
