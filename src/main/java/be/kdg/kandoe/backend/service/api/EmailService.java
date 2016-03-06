package be.kdg.kandoe.backend.service.api;

import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.exceptions.EmailServiceException;

import java.util.List;

public interface EmailService {
    void inviteUnexistingUsersToOrganization(Organization organization, User requester, List<String> emails) throws EmailServiceException;
    void inviteUsersToOrganization(Organization organization, User requester, List<User> users) throws EmailServiceException;
    
    void sendSessionInvitationToUser(Session session, User organizer, User user);
}
