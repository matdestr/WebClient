package be.kdg.kandoe.backend.model.sessions;

import be.kdg.kandoe.backend.model.users.User;
import lombok.*;

import javax.persistence.*;

/**
 * This class holds information about the status of a participant of a session.
 */
@Data
@Entity
public class ParticipantInfo {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private int participantInfoId;
    
    @ManyToOne(targetEntity = User.class, optional = false, cascade = {CascadeType.REFRESH, CascadeType.MERGE}, fetch = FetchType.EAGER)
    private User participant;

    private int joinNumber;
    private boolean joined;
    private boolean addedCardsCompleted;
    private boolean reviewingCardsCompleted;

}
