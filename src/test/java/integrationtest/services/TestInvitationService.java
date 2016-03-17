package integrationtest.services;

import be.kdg.kandoe.backend.config.BackendContextConfig;
import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.users.Invitation;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.InvitationService;
import be.kdg.kandoe.backend.service.api.OrganizationService;
import be.kdg.kandoe.backend.service.api.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { BackendContextConfig.class })
@Transactional
public class TestInvitationService {
    @Autowired
    private InvitationService invitationService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private UserService userService;

    @Before
    public void setUp(){
        User user = new User("username", "pass");
        user.setEmail("test@localhost");
        userService.addUser(user);

        Organization organization = new Organization("TestOrganization", user);
        organizationService.addOrganization(organization);
    }

    @Test
    public void testCreateInvitation(){
        User user = userService.getUserByUsername("username");
        Organization organization = organizationService.getOrganizationByName("TestOrganization");

        Invitation invitation = invitationService.generateInvitation(user, organization);

        List<Invitation> invitationList = invitationService.getInvitationsByUserId(user.getUserId());

        assertEquals("Objects should be equal", invitation, invitationList.get(0));
    }
}










