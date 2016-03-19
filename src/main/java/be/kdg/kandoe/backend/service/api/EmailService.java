package be.kdg.kandoe.backend.service.api;

import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.exceptions.EmailServiceException;

import java.util.List;

/**
 * Interface contract for service that invites users using an email protocol.
 */

public interface EmailService {
    /**
     * Send an invitation email to an unexisting user to join an organization
     * @param organization = The organization they want to join
     * @param requester = The person who requests to join the users
     * @param emails = The emails of the persons who have to join the organization
     * @throws EmailServiceException
     */
    void inviteUnexistingUsersToOrganization(Organization organization, User requester, List<String> emails) throws EmailServiceException;

    /**
     * Send an invitation email to an existing user to join an organization
     * @param organization = The organization they want to join
     * @param requester = The person who requests to join the users
     * @param users = The users who have to join the organization
     * @throws EmailServiceException
     */
    void inviteUsersToOrganization(Organization organization, User requester, List<User> users) throws EmailServiceException;

    /**
     * Send an invitation to an existing user to join a session
     * @param session = The session they want to join
     * @param organizer = The person who owns the session
     * @param user = The user who has to join the session
     */
    void sendSessionInvitationToUser(Session session, User organizer, User user);

    /**
     * Send an invitation to an unexisting user to register and join a session
     * @param session = The session they want to join
     * @param organizer = The person who owns the session
     * @param email = The email of the invited participant
     */
    void sendSessionInvitationToUnexistingUser(Session session, User organizer, String email);
}
