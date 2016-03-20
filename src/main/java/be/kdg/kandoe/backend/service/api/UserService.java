package be.kdg.kandoe.backend.service.api;

import be.kdg.kandoe.backend.model.users.User;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Interface contract for managing the {@link User}s of the application.
 * */
public interface UserService extends UserDetailsService {
    /**
     * Adds a new user.
     * 
     * @param user The user to add
     * */
    User addUser(User user);
    
    /**
     * Retrieves a user by the given username.
     * 
     * @param username The username of the user to retrieve
     * @return The user matching the username
     * */
    User getUserByUsername(String username);
    
    /**
     * Retrieves a user by the given email address.
     * 
     * @param email The email of the user to retrieve
     * @return The user matching the email address
     * */
    User getUserByEmail(String email);
    
    /**
     * Retrieves a user by a given unique ID.
     * 
     * @param userId The unique ID of the user to retrieve
     * @return The user matching the unique user ID
     * */
    User getUserByUserId(int userId);
    
    /**
     * Updates the information of the given user.
     * 
     * @param user The user to update the information for
     * @return The updated state of the user
     * */
    User updateUser(User user);
    
    /**
     * Checks whether the given password is correct for the given user.
     * 
     * @param username The username of the user
     * @param verifyPassword The password to check
     * @throws be.kdg.kandoe.backend.service.exceptions.UserServiceException when the password is incorrect
     * */
    void checkLogin(int username, String verifyPassword);
    
    /**
     * Checks whether a given username is still available.
     * 
     * @param username The username to check the existence of
     * @throws be.kdg.kandoe.backend.service.exceptions.UserServiceException when the username is in use
     * */
    void checkUsernameAvailable(String username);

    /**
     * Checks whether a given email address is still available.
     *
     * @param email The email address to check the existence of
     * @throws be.kdg.kandoe.backend.service.exceptions.UserServiceException when the email address is in use
     * */
    void checkEmailAvailable(String email);
}
