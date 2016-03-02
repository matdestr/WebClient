package be.kdg.kandoe.backend.service.api;

import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.users.Invitation;
import be.kdg.kandoe.backend.model.users.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface InvitationService {
    Invitation generateInvitation(User user, Organization organization);
    Invitation saveInvitation(Invitation invitation);
    List<Invitation> getInvitationsByUserId(int userId);
    Invitation getInvitationByAcceptId(String acceptId);
    void invitationAccepted(Invitation invitation);
}
