package be.kdg.kandoe.backend.service.impl;

import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.users.Invitation;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.persistence.api.InvitationRepository;
import be.kdg.kandoe.backend.service.api.InvitationService;
import be.kdg.kandoe.backend.service.exceptions.InvitationServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class InvitationServiceImpl implements InvitationService {
    @Autowired
    private InvitationRepository invitationRepository;

    @Override
    public Invitation generateInvitation(User user, Organization organization) {
        if (user == null)
            throw new InvitationServiceException("user cannot be null");

        if (organization == null)
            throw new InvitationServiceException("organization cannot be null");

        String uuid = UUID.randomUUID().toString();

        Invitation invitation = new Invitation();
        invitation.setOrganization(organization);
        invitation.setInvitedUser(user);
        invitation.setAcceptId(uuid);

        return saveInvitation(invitation);
    }

    @Override
    public Invitation saveInvitation(Invitation invitation) {
        try {
            return invitationRepository.save(invitation);
        } catch (Exception e){
            throw new InvitationServiceException(String.format("Couldn't save invitation %s", invitation.getInvitationId()), e);
        }
    }

    @Override
    public List<Invitation> getInvitationsByUserId(int userId) {
        return invitationRepository.findInvitationsByInvitedUserUserId(userId);
    }

    @Override
    public Invitation getInvitationByAcceptId(String acceptId) {
        if (acceptId == null){
            throw new InvitationServiceException("acceptId cannot be null");
        }

        return invitationRepository.findInvitationByAcceptId(acceptId);
    }

    @Override
    public void invitationAccepted(Invitation invitation) {
        invitationRepository.delete(invitation);
    }
}
