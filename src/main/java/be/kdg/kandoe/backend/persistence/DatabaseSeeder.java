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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
    private void seed() {
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

        List<Tag> tagList = new ArrayList<Tag>();
        Tag tag1 = new Tag();
        tagList.add(tag1);
        tag1.setName("General");
        Tag tag2 = new Tag();
        tagList.add(tag2);
        tag2.setName("Legals");
        Tag tag3 = new Tag();
        tagList.add(tag3);
        tag3.setName("Medical");
        Tag tag4 = new Tag();
        tagList.add(tag4);
        tag4.setName("Music");
        Tag tag5 = new Tag();
        tagList.add(tag5);
        tag5.setName("Business");
        Tag tag6 = new Tag();
        tagList.add(tag6);
        tag6.setName("Games");
        Tag tag7 = new Tag();
        tagList.add(tag7);
        tag7.setName("Kids");
        Tag tag8 = new Tag();
        tagList.add(tag8);
        tag8.setName("Health");
        Tag tag9 = new Tag();
        tagList.add(tag9);
        tag9.setName("Finance");
        Tag tag10 = new Tag();
        tagList.add(tag10);
        tag10.setName("Food");



        tagRepository.save(tagList);


    }

}
