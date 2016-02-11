package be.kdg.kandoe.backend.service.impl;

import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.persistence.api.OrganizationRepository;
import be.kdg.kandoe.backend.service.api.OrganizationService;
import be.kdg.kandoe.backend.service.exceptions.OrganizationServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Wannes on 2/11/2016.
 */
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
    public List<Organization> getOrganizationsByOwner(String owner) throws OrganizationServiceException {
        if (owner == null)
            throw new OrganizationServiceException("owner cannot be null");

        if (owner.isEmpty())
            throw new OrganizationServiceException("owner cannot be empty");

        try {
            return repository.findOrganizationsByOwnerUsername(owner);
        } catch (Exception e){
            throw new OrganizationServiceException(String.format("Can't find organizations for owner %s", owner), e);
        }
    }
}
