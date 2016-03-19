package be.kdg.kandoe.backend.persistence.api;

import be.kdg.kandoe.backend.model.invitations.OrganizationInvitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Proxy interface for the CRUD Repository for the {@link OrganizationInvitation} model
 */

public interface InvitationRepository extends JpaRepository<OrganizationInvitation, Integer> {
    List<OrganizationInvitation> findInvitationsByInvitedUserUserId(int userId);
    OrganizationInvitation findInvitationByAcceptId(String acceptId);
    List<OrganizationInvitation> findInvitationsByEmail(String email);
}
