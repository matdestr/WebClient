/**
 * An invitation to join a session for a certain user
 */
package be.kdg.kandoe.backend.model.sessions;

import be.kdg.kandoe.backend.model.users.User;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Data
@Entity
// TODO : Check if this class is needed
public class SessionInvitation {
    @Id
    @GeneratedValue
    @Setter(value = AccessLevel.NONE)
    private int sessionInvitationId;
    
    @ManyToOne(targetEntity = User.class, optional = false)
    private User invitedUser;
    
    @ManyToOne(targetEntity = Session.class, optional = false)
    private Session session;
}
