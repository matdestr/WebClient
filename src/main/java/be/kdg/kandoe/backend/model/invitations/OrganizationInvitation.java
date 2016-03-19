/**
 * An invitation for a user to join an organization
 */
package be.kdg.kandoe.backend.model.invitations;

import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.users.User;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
public class OrganizationInvitation implements Serializable {
    @Id
    @GeneratedValue
    @Setter(value = AccessLevel.NONE)
    private Integer invitationId;

    @OneToOne(fetch = FetchType.EAGER)
    private User invitedUser;

    @OneToOne(fetch = FetchType.EAGER)
    private Organization organization;

    @Column(nullable = false, updatable = false, unique = true)
    private String acceptId;

    private boolean accepted;

    private String email;
}
