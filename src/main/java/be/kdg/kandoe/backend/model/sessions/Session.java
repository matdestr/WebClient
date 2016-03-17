/**
 *  A sesion belongs to a category or topic and has an organizer
 *  A session contains information about the participants, the cardpositions and chatmessages
 */
package be.kdg.kandoe.backend.model.sessions;

import be.kdg.kandoe.backend.model.cards.CardDetails;
import be.kdg.kandoe.backend.model.cards.CardPosition;
import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Topic;
import be.kdg.kandoe.backend.model.users.User;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

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

    @ManyToOne
    @JoinColumn(nullable = false)
    private Category category;

    @ManyToOne(optional = true)
    private Topic topic;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER, optional = false)
    @JoinColumn(nullable = false, referencedColumnName = "userId")
    private User organizer;

    @OneToMany(
            targetEntity = ParticipantInfo.class,
            cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE},
            fetch = FetchType.EAGER
    )
    @JoinColumn(name = "Session_ParticipantInfo")
    private Set<ParticipantInfo> participantInfo;

    @OneToOne(
            targetEntity = ParticipantInfo.class,
            cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE},
            fetch = FetchType.EAGER
    )
    private ParticipantInfo currentParticipantPlaying;

    @ManyToMany(
            targetEntity = CardDetails.class,
            cascade = {CascadeType.REFRESH, CascadeType.MERGE},
            fetch = FetchType.EAGER)
    private List<CardDetails> winners;

    @OneToMany(targetEntity = CardsChoice.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "Session_CardsChoice")
    @Fetch(FetchMode.SELECT) // Hibernate workaround: fixes duplicate entities when retrieved
    private List<CardsChoice> participantCardChoices;

    @OneToMany(targetEntity = CardPosition.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Fetch(FetchMode.SELECT) // Hibernate workaround: fixes duplicate entities when retrieved
    private List<CardPosition> cardPositions;

    @OneToMany(targetEntity = ChatMessage.class, mappedBy = "session", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Fetch(FetchMode.SELECT) // Hibernate workaround: fixes duplicate entities when retrieved
    private List<ChatMessage> chatMessages;

    private int minNumberOfCardsPerParticipant;
    private int maxNumberOfCardsPerParticipant;
    private boolean participantsCanAddCards;
    private boolean cardCommentsAllowed;
    private SessionStatus sessionStatus;
    private int amountOfCircles;
    private String publicUrl;


    public Session() {
        this.participantInfo = new HashSet<>();
        this.cardPositions = new ArrayList<>();
        this.chatMessages = new ArrayList<>();
        this.participantCardChoices = new ArrayList<>();

        this.sessionStatus = SessionStatus.CREATED;

        this.amountOfCircles = DEFAULT_CIRCLE_AMOUNT;
        this.minNumberOfCardsPerParticipant = DEFAULT_MIN_CARDS_AMOUNT;
        this.maxNumberOfCardsPerParticipant = DEFAULT_MAX_CARDS_AMOUNT;
    }

    public List<ParticipantInfo> getParticipantSequence() {
        return Collections.unmodifiableList(this.participantInfo.stream().sorted((p1, p2) -> Integer.compare(p1.getJoinNumber(), p2.getJoinNumber())).collect(Collectors.toList()));
    }

    public boolean isUserParticipant(int userId) {
        return participantInfo.stream().anyMatch(p -> p.getParticipant().getUserId() == userId);
    }
}
