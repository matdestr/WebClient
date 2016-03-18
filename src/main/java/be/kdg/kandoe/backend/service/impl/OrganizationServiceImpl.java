package be.kdg.kandoe.backend.service.impl;

import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.persistence.api.OrganizationRepository;
import be.kdg.kandoe.backend.service.api.OrganizationService;
import be.kdg.kandoe.backend.service.exceptions.OrganizationServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrganizationServiceImpl implements OrganizationService {
    @Autowired
    private OrganizationRepository organizationRepository;

    @Override
    public Organization addOrganization(Organization organization) throws OrganizationServiceException {
        if (organization == null)
            throw new OrganizationServiceException("Organization cannot be null");

        try {
            return organizationRepository.save(organization);
        } catch (Exception e) {
            throw new OrganizationServiceException(String.format("Organization '%s' cannot be saved", organization.getName()), e);
        }
    }

    @Override
    public Organization getOrganizationById(int id) throws OrganizationServiceException {
        Organization organization = organizationRepository.findOne(id);

        if (organization == null)
            throw new OrganizationServiceException(String.format("Could not find organization with ID %d", id));

        return organization;
    }

    @Override
    public Organization getOrganizationByName(String name) throws OrganizationServiceException {
        if (name == null)
            throw new OrganizationServiceException("name cannot be null");

        if (name.isEmpty())
            throw new OrganizationServiceException("name cannot be empty");

        try {
            return organizationRepository.findOrganizationByName(name);
        } catch (Exception e) {
            throw new OrganizationServiceException(String.format("Can't find organization %s", name), e);
        }
    }

    @Override
    public boolean organizationExists(String name) {
        return organizationRepository.findOrganizationByName(name) != null;
    }

    @Override
    public List<Organization> getOrganizations() {
        return organizationRepository.findAll();
    }

    @Override
    public List<Organization> getOrganizationsByOwner(String ownerUsername) throws OrganizationServiceException {
        if (ownerUsername == null || ownerUsername.isEmpty())
            throw new OrganizationServiceException("owner username cannot be null or empty");

        try {
            return organizationRepository.findOrganizationsByOwnerUsername(ownerUsername);
        } catch (Exception e) {
            throw new OrganizationServiceException(String.format("can't find organizations for owner %s", ownerUsername), e);
        }
    }

    @Override
    public List<Organization> getOrganizationsOfMember(String username) throws OrganizationServiceException {
        if (username == null || username.isEmpty())
            throw new OrganizationServiceException("username cannot be null or empty");

        try {
            return organizationRepository.findOrganizationsByOrganizersUsernameOrOwnerUsername(username, username);
        } catch (Exception e) {
            throw new OrganizationServiceException(String.format("can't find organizations for owner %s", username), e);
        }
    }

    @Override
    public void updateOrganization(Organization organization) throws OrganizationServiceException {
        if (organization == null)
            throw new OrganizationServiceException("Organization cannot be null");
        
        try {
            organizationRepository.save(organization);
        } catch (Exception e) {
            throw new OrganizationServiceException("Couldn't save organization", e);
        }
    }
}
