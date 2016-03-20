package be.kdg.kandoe.backend.model.snapshots;

import be.kdg.kandoe.backend.model.users.User;

import java.time.LocalDateTime;

public class ChatMessageSnapshot {
    private User user;
    private LocalDateTime dateTime;
    private String text;
}
