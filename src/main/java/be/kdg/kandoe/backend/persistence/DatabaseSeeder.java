package be.kdg.kandoe.backend.persistence;

import be.kdg.kandoe.backend.model.oauth.OAuthClientDetails;
import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.organizations.Tag;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.model.users.roles.RoleType;
import be.kdg.kandoe.backend.persistence.api.OAuthClientDetailsRepository;
import be.kdg.kandoe.backend.persistence.api.OrganizationRepository;
import be.kdg.kandoe.backend.persistence.api.TagRepository;
import be.kdg.kandoe.backend.persistence.api.UserRepository;
import lombok.val;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

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

    @Autowired
    private TagRepository tagRepository;



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

        val testUser = new User();
        testUser.setUsername("user");
        testUser.setPassword(passwordEncoder.encode("pass"));
        testUser.setName("Test");
        testUser.setSurname("User");
        testUser.setEmail("test@user.com");
        testUser.setProfilePictureUrl("profilepictures/default.png");
        testUser.addRole(RoleType.ROLE_CLIENT);

        users.add(testUser);

        val adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setPassword(passwordEncoder.encode("admin"));
        adminUser.setName("Admin");
        adminUser.setSurname("User");
        adminUser.setEmail("admin@user.com");
        adminUser.setProfilePictureUrl("profilepictures/default.png");
        adminUser.addRole(RoleType.ROLE_ADMIN, RoleType.ROLE_CLIENT);

        users.add(adminUser);

        val harold = new User();
        harold.setUsername("Harold");
        harold.setPassword(passwordEncoder.encode("harold"));
        harold.setName("Harold");
        harold.setSurname("Hidethepain");
        harold.setEmail("harold@hidethepain.com");
        harold.setProfilePictureUrl("profilepictures/harold.jpg");
        harold.addRole(RoleType.ROLE_CLIENT);

        users.add(harold);

        userRepository.save(users);


        val organisation = new Organization("Organisation 1", adminUser);
        organisation.addMember(testUser);
        organizationRepository.save(organisation);

        Scanner s = null;
        try {
            s = new Scanner(getResourceAsFile("listoftags.rtf"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        List<Tag> tagList = new ArrayList<Tag>();
        while (s.hasNextLine()){
            Tag tag = new Tag();
            tag.setName(s.nextLine());
            tagList.add(tag);
        }
        s.close();
        tagRepository.save(tagList);





    }

    public static File getResourceAsFile(String resourcePath) {
        try {
            InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(resourcePath);
            if (in == null) {
                return null;
            }

            File tempFile = File.createTempFile(String.valueOf(in.hashCode()), ".tmp");
            tempFile.deleteOnExit();

            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                //copy stream
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
