package be.kdg.kandoe.backend.service.impl;

import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.persistence.api.OrganizationRepository;
import be.kdg.kandoe.backend.service.api.OrganizationService;
import be.kdg.kandoe.backend.service.exceptions.OrganizationServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrganizationServiceImpl implements OrganizationService {
    @Autowired
    private OrganizationRepository repository;

    @Override
    public Organization addOrganization(Organization organization) throws OrganizationServiceException {
        if (organization == null)
            throw new OrganizationServiceException("Organization cannot be null");

        try {
            return repository.save(organization);
        } catch (Exception e){
            throw new OrganizationServiceException(String.format("Organization '%s' cannot be saved", organization.getName()), e);
        }
    }

    @Override
    public Organization getOrganizationById(int id) throws OrganizationServiceException {
        Organization organization = repository.findOne(id);
        
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
            return repository.findOrganizationByName(name);
        } catch (Exception e){
            throw new OrganizationServiceException(String.format("Can't find organization %s", name), e);
        }
    }

    @Override
    public List<Organization> getOrganizationsByOwner(String ownerUsername) throws OrganizationServiceException {
        if (ownerUsername == null || ownerUsername.isEmpty())
            throw new OrganizationServiceException("owner username cannot be null or empty");
        
        try {
            return repository.findOrganizationsByOwnerUsername(ownerUsername);
        } catch (Exception e){
            throw new OrganizationServiceException(String.format("can't find organizations for owner %s", ownerUsername), e);
        }
    }

    @Override
    public List<Organization> getOrganizationsByUser(String username) throws OrganizationServiceException {
        //TODO replace with more performant method
        if (username == null || username.isEmpty()) {
            throw new OrganizationServiceException("username cannot be null");
        }

        List<Organization> organizations = repository.findAll();
        List<Organization> userOrganizations = new ArrayList<>();

        for (Organization organization : organizations) {
            organization.getMembers()
                    .stream()
                    .filter(user -> user.getName().equals(username))
                    .forEach(user -> userOrganizations.add(organization));
        }

        return userOrganizations;
    }
}
