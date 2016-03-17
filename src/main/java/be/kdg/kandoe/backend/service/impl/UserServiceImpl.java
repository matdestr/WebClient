package be.kdg.kandoe.backend.service.impl;

import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.persistence.api.UserRepository;
import be.kdg.kandoe.backend.service.api.UserService;
import be.kdg.kandoe.backend.service.exceptions.UserServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User addUser(User user) {
        validateUser(user);
        try {
            String unencryptedPassword = user.getPassword();
            String encryptedPassword = passwordEncoder.encode(unencryptedPassword);
            user.setPassword(encryptedPassword);

            return userRepository.save(user);
        } catch (Exception e) {
            throw new UserServiceException(String.format("Could not save user with username %s", user.getUsername()));
        }
    }

    private void validateUser(User user){
        if (user == null)
            throw new UserServiceException("User cannot be null");

        if (user.getUsername() == null || user.getUsername().trim().isEmpty()){
            throw new UserServiceException("username of user cannot be null or empty");
        }

        if (user.getEmail() == null || user.getUsername().trim().isEmpty()){
            throw new UserServiceException("email of user cannot be null or empty");
        }
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public User getUserByUserId(int userId) {
        return userRepository.findOne(userId);
    }

    @Override
    public User updateUser(User user) {
        validateUser(user);
        return userRepository.save(user);
    }

    @Override
    public void checkLogin(int userId, String password) {
        User u = userRepository.findOne(userId);
        if (u == null || !passwordEncoder.matches(password, u.getPassword())) {
            throw new UserServiceException("Username or password isn't correct");
        }
    }

    @Override
    public void checkUsernameAvailable(String username) {
        User u = userRepository.findUserByUsername(username);
        if (u != null)
            throw new UserServiceException("Username is already taken.");
    }

    @Override
    public void checkEmailAvailable(String email) {
        User u = userRepository.findUserByEmail(email);
        if (u != null)
            throw new UserServiceException("Email is already taken.");
    }

    @Override
        public UserDetails loadUserByUsername (String username)throws UsernameNotFoundException {
            User user = this.getUserByUsername(username);

            if (user == null)
                throw new UsernameNotFoundException(String.format("No user with username %s found", username));

            return user;
        }
    }
