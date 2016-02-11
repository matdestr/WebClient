package unittest;

import be.kdg.kandoe.backend.config.BackendContextConfig;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.UserService;
import be.kdg.kandoe.backend.service.exceptions.UserServiceException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { BackendContextConfig.class })
@Transactional // Automatically rollbacks after each test
public class TestRegisterAndLogin {
    @Autowired
    private UserService userService;

    @Test
    public void testAddNewUser() {
        User user = new User("username", "password");
        userService.addUser(user);
        User fetchedUser = userService.getUserByUsername("username");
        assertEquals(user, fetchedUser);
    }

    @Test(expected = UserServiceException.class)
    public void testTryAddExistingUser() {
        User user = new User("username", "password");
        userService.addUser(user);

        // Need to create new user, otherwise the repository sees the ID and updates the record
        User duplicateUser = new User("username", "password");
        userService.addUser(duplicateUser);
    }
}
