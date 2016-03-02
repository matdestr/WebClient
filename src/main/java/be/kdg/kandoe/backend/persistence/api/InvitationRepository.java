package be.kdg.kandoe.backend.persistence.api;

import be.kdg.kandoe.backend.model.users.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvitationRepository extends JpaRepository<Invitation, Integer> {
    List<Invitation> findInvitationsByInvitedUserUserId(int userId);
    Invitation findInvitationByAcceptId(String acceptId);
}
