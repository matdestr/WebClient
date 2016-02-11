package be.kdg.kandoe.backend.service.api;

import be.kdg.kandoe.backend.model.users.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.ClientDetailsService;

public interface UserService extends UserDetailsService {
    User addUser(User user);
    User getUserByUsername(String username);
}
