package be.kdg.kandoe.backend.service.api;

import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.exceptions.OrganizationServiceException;

import java.util.List;

public interface OrganizationService {
    Organization addOrganization(Organization organization) throws OrganizationServiceException;
    Organization getOrganizationById(int id) throws OrganizationServiceException;
    Organization getOrganizationByName(String name) throws OrganizationServiceException;
    boolean organizationExists(String name);
    List<Organization> getOrganizations();
    List<Organization> getOrganizationsByOwner(String owner) throws OrganizationServiceException;
    List<Organization> getOrganizationsOfMember(String username) throws OrganizationServiceException;
    void updateOrganization(Organization organization) throws OrganizationServiceException;
}
