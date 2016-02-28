package be.kdg.kandoe.backend.model.sessions;


import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.organizations.Topic;
import be.kdg.kandoe.backend.model.users.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int sessionId;
    @OneToMany(targetEntity = ChatMessage.class, mappedBy = "session", fetch = FetchType.EAGER)
    private List<ChatMessage> chatMessages;
    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    private User organizer;
    @OneToMany(targetEntity = User.class, fetch = FetchType.EAGER)
    private List<User> participants;
    @ManyToOne
    private Topic topic;
    @ManyToOne
    private Organization organization;
    public abstract String getPublicUrl();
    private boolean commentsAllowed;
    private boolean userCanAddCards;
    private int minNumberOfCards;
    private int maxNumberOfCards;
    public abstract void endSession();
    public abstract boolean isFinished();

    public Session(){
        this.participants = new ArrayList<>();
        this.chatMessages = new ArrayList<>();
    }

    public boolean isUserParticipant(int userId){
        return participants.stream().anyMatch(u -> u.getUserId() == userId);
    }

    public boolean addParticipant(User user){
        if (user == null){
            throw new NullPointerException("user cannot be null");
        }

        if (isUserParticipant(user.getUserId())){
            return false;
        }

        this.participants.add(user);
        return true;
    }
}
