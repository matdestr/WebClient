package be.kdg.kandoe.backend.persistence;

import be.kdg.kandoe.backend.model.oauth.OAuthClientDetails;
import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.model.users.roles.RoleType;
import be.kdg.kandoe.backend.persistence.api.OAuthClientDetailsRepository;
import be.kdg.kandoe.backend.persistence.api.OrganizationRepository;
import be.kdg.kandoe.backend.persistence.api.UserRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

@Component
public class DatabaseSeeder {
    @Autowired
    private OAuthClientDetailsRepository clientDetailsRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @PostConstruct
    private void seed(){
        OAuthClientDetails clientDetails = new OAuthClientDetails("webapp");
        
        clientDetails.setAuthorizedGrandTypes("password", "authorization_code", "refresh_token", "client_credentials");
        clientDetails.setAuthorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT");
        clientDetails.setScopes("read", "write", "trust");
        clientDetails.setSecret("secret");
        clientDetails.setAccessTokenValiditySeconds(60 * 60);
        
        clientDetailsRepository.save(clientDetails);
        
        OAuthClientDetails clientDetailsAndroid = new OAuthClientDetails("android");
        
        clientDetailsAndroid.setAuthorizedGrandTypes("password", "refresh_token");
        clientDetailsAndroid.setAuthorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT");
        clientDetailsAndroid.setScopes("read", "write", "trust");
        clientDetailsAndroid.setSecret("secret");
        clientDetailsAndroid.setAccessTokenValiditySeconds(60 * 60);
        
        clientDetailsRepository.save(clientDetailsAndroid);

        val users = new ArrayList<User>();

        val test = new User();
        test.setUsername("user");
        test.setPassword(passwordEncoder.encode("pass"));
        test.setName("Test");
        test.setSurname("User");
        test.setEmail("test@user.com");
        test.addRole(RoleType.ROLE_CLIENT);

        users.add(test);

        val admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setName("Admin");
        admin.setSurname("User");
        admin.setEmail("admin@user.com");
        admin.addRole(RoleType.ROLE_ADMIN, RoleType.ROLE_CLIENT);

        users.add(admin);

        val harold = new User();
        harold.setUsername("Harold");
        harold.setPassword(passwordEncoder.encode("harold"));
        harold.setName("Harold");
        harold.setSurname("Hidethepain");
        harold.setEmail("harold@hidethepain.com");
        harold.addRole(RoleType.ROLE_CLIENT);

        users.add(harold);

        userRepository.save(users);


        val organisation = new Organization("Organisation 1", admin);
        organizationRepository.save(organisation);
    }
}
