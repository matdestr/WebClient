package be.kdg.kandoe.frontend.controller.resources.sessions;

import be.kdg.kandoe.backend.model.sessions.SessionStatus;
import be.kdg.kandoe.frontend.controller.resources.users.UserResource;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Resource for the {@link be.kdg.kandoe.backend.model.sessions.Session}
 */

@Data
public class SessionResource {

    private int sessionId;
    private int categoryId;
    private Integer topicId;
    private UserResource organizer;
    private List<ParticipantInfoResource> participantInfo;
    //private int currentParticipantPlayingUserId;
    private UserResource currentParticipantPlaying;
    //private List<CardPositionResource> cardPositions;
    private int minNumberOfCardsPerParticipant;
    private int maxNumberOfCardsPerParticipant;
    private boolean participantsCanAddCards;
    private boolean cardCommentsAllowed;
    private SessionStatus sessionStatus;
    private int amountOfCircles;
    
    public SessionResource() {
        this.participantInfo = new ArrayList<>();
        //this.cardPositions = new ArrayList<>();
    }
}
