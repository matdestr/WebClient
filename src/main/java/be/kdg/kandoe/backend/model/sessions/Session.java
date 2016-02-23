package be.kdg.kandoe.backend.model.sessions;


import be.kdg.kandoe.backend.model.organizations.Topic;
import be.kdg.kandoe.backend.model.users.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int sessionId;
    @OneToMany(targetEntity = ChatMessage.class, mappedBy = "session", fetch = FetchType.EAGER)
    private List<ChatMessage> chatMessages;
    private User organizer;
    @OneToMany(targetEntity = User.class, fetch = FetchType.EAGER)
    private List<User> participants;
    @ManyToOne
    private Topic topic;
    public abstract String getPublicUrl();
    private boolean commentsAllowed;
    private boolean userCanAddCards;
    private int minNumberOfCards;
    private int maxNumberOfCards;
    public abstract void endSession();
    public abstract boolean isFinished();
}
