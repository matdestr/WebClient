package be.kdg.kandoe.backend.service.api;

import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.service.exceptions.OrganizationServiceException;

import java.util.List;

/**
 * Interface contract for Service of the {@link Organization} model
 */
public interface OrganizationService {
    Organization addOrganization(Organization organization) throws OrganizationServiceException;
    
    /**
     * Retrieves an organization by its given unique ID
     * 
     * @param id The unique ID of the organization.
     * */
    Organization getOrganizationById(int id) throws OrganizationServiceException;
    
    /**
     * Retrieves an organization by its given name.
     * 
     * @param name The name of the organization.
     * */
    Organization getOrganizationByName(String name) throws OrganizationServiceException;
    List<Organization> getOrganizations();
    
    /**
     * Retrieves a list of organizations owned by a user.
     * 
     * @param owner The username of the owner of the organization
     * */
    List<Organization> getOrganizationsByOwner(String owner) throws OrganizationServiceException;
    
    /**
     * Retrieves a list of organizations of which the user with given username is a member.
     * 
     * @param username The username of the member
     * */
    List<Organization> getOrganizationsOfMember(String username) throws OrganizationServiceException;
    
    /**
     * Updates the state of an organization.
     * */
    void updateOrganization(Organization organization) throws OrganizationServiceException;

    /**
     * Check if an organization already exists.
     * 
     * @param name = The name of the organization
     * @return true if the organization exists, false if it does not.
     */
    boolean organizationExists(String name);
}
