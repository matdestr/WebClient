package be.kdg.kandoe.backend.service.impl;

import be.kdg.kandoe.backend.model.invitations.OrganizationInvitation;
import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.persistence.api.InvitationRepository;
import be.kdg.kandoe.backend.service.api.InvitationService;
import be.kdg.kandoe.backend.service.exceptions.InvitationServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class InvitationServiceImpl implements InvitationService {
    @Autowired
    private InvitationRepository invitationRepository;

    @Override
    public OrganizationInvitation generateInvitation(User user, Organization organization) {
        if (user == null)
            throw new InvitationServiceException("user cannot be null");

        if (organization == null)
            throw new InvitationServiceException("organization cannot be null");

        String uuid = UUID.randomUUID().toString();

        OrganizationInvitation invitation = new OrganizationInvitation();
        invitation.setOrganization(organization);
        invitation.setInvitedUser(user);
        invitation.setAcceptId(uuid);
        invitation.setEmail(user.getEmail());

        return saveInvitation(invitation);
    }

    @Override
    public OrganizationInvitation generateInvitationForUnexistingUser(String email, Organization organization) {
        if (email == null)
            throw new InvitationServiceException("email cannot be null");

        if (organization == null)
            throw new InvitationServiceException("organization cannot be null");

        String uuid = UUID.randomUUID().toString();

        OrganizationInvitation invitation = new OrganizationInvitation();
        invitation.setAcceptId(uuid);
        invitation.setOrganization(organization);
        invitation.setEmail(email);
        invitation.setInvitedUser(null);

        return saveInvitation(invitation);
    }

    @Override
    public OrganizationInvitation generateInvitationForUnexistingUser(String email, Session session) {
        return null;
    }

    @Override
    public OrganizationInvitation saveInvitation(OrganizationInvitation invitation) {
        try {
            return invitationRepository.save(invitation);
        } catch (Exception e){
            throw new InvitationServiceException("Couldn't save invitation", e);
        }
    }

    @Override
    public List<OrganizationInvitation> getInvitationsByUserId(int userId) {
        return invitationRepository.findInvitationsByInvitedUserUserId(userId);
    }

    @Override
    public OrganizationInvitation getInvitationByAcceptId(String acceptId) {
        if (acceptId == null){
            throw new InvitationServiceException("acceptId cannot be null");
        }

        return invitationRepository.findInvitationByAcceptId(acceptId);
    }

    @Override
    public void invalidateInvitation(OrganizationInvitation invitation) {
        invitationRepository.delete(invitation);
    }

    @Override
    public List<OrganizationInvitation> getInvitationsByEmail(String email) {
        if (email == null)
            throw new InvitationServiceException("email cannot be null");

        return invitationRepository.findInvitationsByEmail(email);
    }
}
