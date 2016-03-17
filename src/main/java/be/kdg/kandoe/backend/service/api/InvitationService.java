/**
 * Interface to manage the invitations
 */
package be.kdg.kandoe.backend.service.api;

import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.users.Invitation;
import be.kdg.kandoe.backend.model.users.User;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Interface contract for service of {@link Invitation} model
 */

public interface InvitationService {
    /**
     * Generates an invitation for an existing user to an organization
     * @param user = The person wants to join the organization
     * @param organization = The organization the user wants to join
     * @return
     */
    Invitation generateInvitation(User user, Organization organization);

    /**
     * Generates an invitation for an unexisting user to an organization
     * @param email = The email of the person wants to join the organization
     * @param organization = The organization the user wants to join
     * @return
     */
    Invitation generateInvitationForUnexistingUser(String email, Organization organization);

    Invitation saveInvitation(Invitation invitation);
    List<Invitation> getInvitationsByUserId(int userId);
    Invitation getInvitationByAcceptId(String acceptId);

    /**
     * Get all invitations for an email
     */
    List<Invitation> getInvitationsByEmail(String email);

    /**
     * Delete or invalidate an invitation
     * @param invitation = The invitation that needs to be invalidated
     */
    void invalidateInvitation(Invitation invitation);
}
