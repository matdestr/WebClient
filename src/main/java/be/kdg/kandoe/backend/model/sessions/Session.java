package be.kdg.kandoe.backend.model.sessions;

import be.kdg.kandoe.backend.model.cards.CardDetails;
import be.kdg.kandoe.backend.model.cards.CardPosition;
import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Topic;
import be.kdg.kandoe.backend.model.users.User;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Session {
    public static final int MAX_PLAYERS = 6;
    public static final int MIN_CIRCLE_AMOUNT = 3;
    public static final int MAX_CIRCLE_AMOUNT = 6;
    
    public static final int DEFAULT_CIRCLE_AMOUNT = 5;
    public static final int DEFAULT_MIN_CARDS_AMOUNT = 4;
    public static final int DEFAULT_MAX_CARDS_AMOUNT = 5;
    
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Setter(AccessLevel.NONE)
    private int sessionId;

    /*@ManyToOne
    private Organization organization;*/
    
    @ManyToOne
    @JoinColumn(nullable = false)
    private Category category;
    
    @ManyToOne(optional = true)
    private Topic topic;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER, optional = false)
    @JoinColumn(nullable = false, referencedColumnName = "userId")
    private User organizer;
    
    /*// TODO : Fix FK reference constraint creation
    @ManyToMany(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinTable(
            name = "SessionParticipants",
            joinColumns = @JoinColumn(name = "session_id"),
            inverseJoinColumns = @JoinColumn(name = "participant_userId"),
            foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT),
            inverseForeignKey = @ForeignKey(ConstraintMode.CONSTRAINT),
            uniqueConstraints = {
                    @UniqueConstraint(columnNames = { "session_id", "participant_userId"})
            }
    )
    private List<User> participants;*/
    
    @OneToMany(
            targetEntity = ParticipantInfo.class,
            cascade = { CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE },
            fetch = FetchType.EAGER
    )
    private Set<ParticipantInfo> participantInfo;
    
    private User currentParticipantPlaying;

    /*@OneToMany(targetEntity = CardDetails.class, fetch = FetchType.EAGER)
    private Set<CardDetails> cards;*/

    @OneToMany(targetEntity = CardsChoice.class, fetch = FetchType.EAGER)
    private List<CardsChoice> participantCardChoices;
    
    @OneToMany(targetEntity = CardPosition.class, fetch = FetchType.EAGER)
    private List<CardPosition> cardPositions;

    @OneToMany(targetEntity = ChatMessage.class, mappedBy = "session", fetch = FetchType.EAGER)
    private List<ChatMessage> chatMessages;
    
    private int minNumberOfCardsPerParticipant;
    private int maxNumberOfCardsPerParticipant;
    private boolean participantsCanAddCards;
    private boolean cardCommentsAllowed;
    private SessionStatus sessionStatus;
    private int amountOfCircles;
    private String publicUrl;

    public Session(){
        //this.participants = new ArrayList<>();
        this.participantInfo = new HashSet<>();
        //this.cards = new HashSet<>();
        this.cardPositions = new ArrayList<>();
        this.chatMessages = new ArrayList<>();
        this.participantCardChoices = new ArrayList<>();
        
        this.sessionStatus = SessionStatus.CREATED;
        
        this.amountOfCircles = DEFAULT_CIRCLE_AMOUNT;
        this.minNumberOfCardsPerParticipant = DEFAULT_MIN_CARDS_AMOUNT;
        this.maxNumberOfCardsPerParticipant = DEFAULT_MAX_CARDS_AMOUNT;
    }

    public boolean isUserParticipant(int userId){
        //return participants.stream().anyMatch(u -> u.getUserId() == userId);
        return participantInfo.stream().anyMatch(p -> p.getParticipant().getUserId() == userId);
    }

    /*public boolean addParticipant(User user){
        if (user == null){
            throw new NullPointerException("user cannot be null");
        }

        if (isUserParticipant(user.getUserId())){
            return false;
        }

        this.participants.add(user);
        
        return true;
    }*/
}
