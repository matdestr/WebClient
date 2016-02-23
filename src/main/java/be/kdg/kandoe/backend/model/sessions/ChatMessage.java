package be.kdg.kandoe.backend.model.sessions;

import be.kdg.kandoe.backend.model.users.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@Entity
public class ChatMessage {
    @Id
    @GeneratedValue
    private int chatMessageId;
    @ManyToOne
    private User user;
    @ManyToOne
    private Session session;
    private String content;
    private LocalDateTime dateTime;
}
