package integrationtest.services;

import be.kdg.kandoe.backend.config.BackendContextConfig;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.model.users.roles.RoleType;
import be.kdg.kandoe.backend.service.api.UserService;
import be.kdg.kandoe.backend.service.exceptions.UserServiceException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { BackendContextConfig.class })
@Transactional // Automatically rollbacks after each test
public class TestUserService {
    @Autowired
    private UserService userService;

    @Test
    public void testAddNewUser() {
        User user = new User("username", "password");
        user.setEmail("test@localhost");
        userService.addUser(user);
        User fetchedUser = userService.getUserByUsername("username");
        assertEquals(user, fetchedUser);
    }

    @Test(expected = UserServiceException.class)
    public void testTryAddExistingUser() {
        User user = new User("username", "password");
        user.setEmail("test@localhost");
        userService.addUser(user);

        // Need to create new user, otherwise the repository sees the ID and updates the record
        User duplicateUser = new User("username", "password");
        userService.addUser(duplicateUser);
    }

    @Test
    public void testUpdateUser() throws Exception
    {
        User user = new User("username", "password");
        user.setEmail("test@localhost");
        userService.addUser(user);
        User fetchedUser = userService.getUserByUsername("username");

        String email = "test@localhost.com";
        fetchedUser.setEmail(email);

        userService.updateUser(fetchedUser);
        fetchedUser = userService.getUserByUsername("username");
        assertThat(fetchedUser.getEmail(), equalTo(email));
    }

    @Test
    public void testAddRoleToUser() throws Exception {
        User user = new User("username", "password");
        user.setEmail("testroluser@localhost");
        user.addRole(RoleType.ROLE_ADMIN);

        User savedUser = userService.addUser(user);
        User fetchedUser = userService.getUserByUserId(savedUser.getUserId());

        assertEquals(savedUser.getRoles().size(), fetchedUser.getRoles().size());

    }
}
