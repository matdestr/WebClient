package be.kdg.kandoe.backend.persistence.api;

import be.kdg.kandoe.backend.model.organizations.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrganizationRepository extends JpaRepository<Organization, Integer> {
    Organization findOrganizationByName(String name);
    List<Organization> findOrganizationsByOwnerUsername(String owner);
    List<Organization> findOrganizationsByOrganizersUsername(String username);
    List<Organization> findOrganizationsByOrganizersUsernameOrOwnerUsername(String memberName, String ownerName);
}
