package be.kdg.kandoe.backend.model.sessions;

import be.kdg.kandoe.backend.model.users.User;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * This class represents a message sent during a session.
 * Has a content and a time
 * Belongs to a session and a user
 */
@Data
@NoArgsConstructor
@Entity
public class ChatMessage {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private int chatMessageId;
    
    @ManyToOne(optional = false)
    private User user;
    
    @ManyToOne(optional = false)
    private Session session;
    
    private String content;
    private LocalDateTime dateTime;
}
