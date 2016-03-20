/**
 * Interface to manage the invitations
 */
package be.kdg.kandoe.backend.service.api;

import be.kdg.kandoe.backend.model.invitations.OrganizationInvitation;
import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.backend.model.users.User;

import java.util.List;

/**
 * Interface contract for service of {@link OrganizationInvitation} model
 */

public interface InvitationService {
    /**
     * Generates an invitation for an existing user to an organization
     * @param user = The person wants to join the organization
     * @param organization = The organization the user wants to join
     * @return
     */
    OrganizationInvitation generateInvitation(User user, Organization organization);

    /**
     * Generates an invitation for an unexisting user to an organization
     * @param email = The email of the person wants to join the organization
     * @param organization = The organization the user wants to join
     * @return invitation = The created invitation
     */
    OrganizationInvitation generateInvitationForUnexistingUser(String email, Organization organization);
    OrganizationInvitation generateInvitationForUnexistingUser(String email, Session session);

    OrganizationInvitation saveInvitation(OrganizationInvitation invitation);
    List<OrganizationInvitation> getInvitationsByUserId(int userId);
    OrganizationInvitation getInvitationByAcceptId(String acceptId);

    /**
     * Get all invitations for an email
     */
    List<OrganizationInvitation> getInvitationsByEmail(String email);

    /**
     * Delete or invalidate an invitation
     * @param invitation = The invitation that needs to be invalidated
     */
    void invalidateInvitation(OrganizationInvitation invitation);
}
