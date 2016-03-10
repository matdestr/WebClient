package integrationtest.services;

import be.kdg.kandoe.backend.config.BackendContextConfig;
import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.OrganizationService;
import be.kdg.kandoe.backend.service.api.UserService;
import be.kdg.kandoe.backend.service.exceptions.OrganizationServiceException;
import be.kdg.kandoe.backend.service.exceptions.UserServiceException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {BackendContextConfig.class })
@Transactional
@Rollback
public class TestOrganizationService {
    @Autowired
    private UserService userService;

    @Autowired
    private OrganizationService organizationService;

    @Before
    public void initialize(){
        User user = new User("username", "pass");
        userService.addUser(user);
    }

    @Test
    public void addNewOrganization() throws UserServiceException {
        User user = userService.getUserByUsername("user");
        Organization organization = new Organization("Organization 1", user);
        Organization saved = organizationService.addOrganization(organization);

        assertEquals(organization, saved);
    }

    @Test(expected = OrganizationServiceException.class)
    public void addExistingOrganization() throws UserServiceException {
        User user = userService.getUserByUsername("user");
        Organization organization1 = new Organization("Organization 1", user);
        Organization organization2 = new Organization("Organization 1", user);

        organizationService.addOrganization(organization1);
        organizationService.addOrganization(organization2);
    }

    @Test
    public void getExistingOrganizationByName() throws UserServiceException {
        String organizationName = "Organization 1";
        User user = userService.getUserByUsername("user");

        Organization organization = new Organization(organizationName, user);
        organizationService.addOrganization(organization);

        Organization existing = organizationService.getOrganizationByName(organizationName);
        assertEquals(organization, existing);
    }

    @Test
    public void getUnexistingOrganizationByName() {
        Organization existing = organizationService.getOrganizationByName("Organization 2");
        assertNull(existing);
    }

    @Test(expected = OrganizationServiceException.class)
    public void getUnexistingOrganizationByNull() {
        organizationService.getOrganizationByName(null);
    }

    @Test(expected = OrganizationServiceException.class)
    public void getUnexistingOrganisationByEmptyName(){
        organizationService.getOrganizationByName("");
    }

    @Test
    public void getOrganizationsByExistingOwner() throws UserServiceException {
        String organizationOne = "Organization 1";
        String organizationTwo = "Organization 2";
        
        //User user = userService.getUserByUsername("user");
        User user = userService.addUser(new User("test-user", "pass"));

        Organization organization1 = new Organization(organizationOne, user);
        Organization organization2 = new Organization(organizationTwo, user);
        organizationService.addOrganization(organization1);
        organizationService.addOrganization(organization2);

        List<Organization> existing = organizationService.getOrganizationsByOwner("test-user");

        assertEquals("list should contain 2 items", 2, existing.size());
    }

    @Test
    public void getOrganizationsByUnexistingOwner() throws UserServiceException {
        List<Organization> unexisting = organizationService.getOrganizationsByOwner("unexisting");

        assertEquals("list should be empty", 0, unexisting.size());
    }

    @Test(expected = OrganizationServiceException.class)
    public void getOrganizationsByNullOwner() throws UserServiceException {
        organizationService.getOrganizationsByOwner(null);
    }

    @Test(expected = OrganizationServiceException.class)
    public void getOrganizationsByEmptyOwner()  {
        organizationService.getOrganizationsByOwner("");
    }

    @Test
    public void getOrganizationsByUser(){
        User organizationUser = new User("user-in-organisation", "password");
        userService.addUser(organizationUser);

        Organization organization = new Organization("Organization 1", organizationUser);
        Organization saved = organizationService.addOrganization(organization);

        saved.addMember(organizationUser);

        organizationService.updateOrganization(saved);

        List<Organization> organizations = organizationService.getOrganizationsOfMember(organizationUser.getUsername());
        assertEquals(organizations.size(), 1);
    }

    @Test(expected = OrganizationServiceException.class)
    public void getOrganizationsByNullUser() {
        organizationService.getOrganizationsOfMember(null);
    }

    @Test(expected = OrganizationServiceException.class)
    public void getOrganizationsByEmptyUser() {
        organizationService.getOrganizationsOfMember("");
    }



}
