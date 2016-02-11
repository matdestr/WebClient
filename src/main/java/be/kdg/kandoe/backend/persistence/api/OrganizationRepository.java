package be.kdg.kandoe.backend.persistence.api;

import be.kdg.kandoe.backend.model.organizations.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Wannes on 2/11/2016.
 */
public interface OrganizationRepository extends JpaRepository<Organization, Integer> {
    Organization findOrganizationByName(String name);
    List<Organization> findOrganizationsByOwnerUsername(String owner);
}
