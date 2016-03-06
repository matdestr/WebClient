package be.kdg.kandoe.backend.persistence;

import be.kdg.kandoe.backend.model.cards.CardDetails;
import be.kdg.kandoe.backend.model.oauth.OAuthClientDetails;
import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.organizations.Tag;
import be.kdg.kandoe.backend.model.organizations.Topic;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.model.users.roles.RoleType;
import be.kdg.kandoe.backend.persistence.api.*;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

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
    private CategoryRepository categoryRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private CardDetailsRepository cardDetailsRepository;

    @PostConstruct
    private void seed() {
        seedOAuthClientDetails();
        seedUsersCategoriesTopicsAndSessions();
        seedTags();
    }
    
    private void seedOAuthClientDetails() {
        OAuthClientDetails clientDetails = new OAuthClientDetails("webapp");

        clientDetails.setAuthorizedGrandTypes("password", "authorization_code", "refresh_token", "client_credentials");
        clientDetails.setAuthorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT");
        clientDetails.setScopes("read", "write", "trust");
        clientDetails.setSecret("secret");
        //clientDetails.setAccessTokenValiditySeconds(60 * 60);
        clientDetails.setAccessTokenValiditySeconds(604_800);

        clientDetailsRepository.save(clientDetails);

        OAuthClientDetails clientDetailsAndroid = new OAuthClientDetails("android");

        clientDetailsAndroid.setAuthorizedGrandTypes("password", "refresh_token");
        clientDetailsAndroid.setAuthorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT");
        clientDetailsAndroid.setScopes("read", "write", "trust");
        clientDetailsAndroid.setSecret("secret");
        //clientDetailsAndroid.setAccessTokenValiditySeconds(60 * 60);
        clientDetailsAndroid.setAccessTokenValiditySeconds(604_800);

        clientDetailsRepository.save(clientDetailsAndroid);
    }
    
    private void seedUsersCategoriesTopicsAndSessions() {
        val users = new ArrayList<User>();
        
        User testUser = new User("user", passwordEncoder.encode("pass"));
        testUser.setName("Test");
        testUser.setSurname("User");
        testUser.setEmail("testuser@cando.com");
        testUser.setProfilePictureUrl("profilepictures/default.png");
        testUser.addRole(RoleType.ROLE_CLIENT);

        User harold = new User();
        harold.setUsername("harold");
        harold.setPassword(passwordEncoder.encode("harold"));
        harold.setName("Harold");
        harold.setSurname("Painhider");
        harold.setEmail("harold@haroldmail.pain");
        harold.setProfilePictureUrl("profilepictures/harold.jpg");
        harold.addRole(RoleType.ROLE_CLIENT);
        
        users.add(testUser);
        users.add(harold);
        
        userRepository.save(users);

        Organization organization = new Organization("Pain hiders", testUser);
        organization.addMember(harold);
        organization = organizationRepository.save(organization);
        
        Category category1 = new Category();
        category1.setOrganization(organization);
        category1.setName("Category 1");
        category1.setDescription("Description of the first category");
        category1 = categoryRepository.save(category1);
        
        Topic topic1 = new Topic();
        topic1.setCategory(category1);
        topic1.setName("Topic 1");
        topic1.setDescription("Description of the first topic");
        topic1 = topicRepository.save(topic1);
        
        for (int i = 0; i < 10; i++) {
            CardDetails cardDetails = new CardDetails();
            cardDetails.setCategory(category1);
            cardDetails.setCreator(testUser);
            cardDetails.setText("Card " + (i + 1));
            
            cardDetailsRepository.save(cardDetails);
        }
    }

    private void seedTags() {
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
    
    private void seedOldData() {
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
        harold.setSurname("Painhider");
        harold.setEmail("harold@haroldmail.com");
        harold.setProfilePictureUrl("profilepictures/harold.jpg");
        harold.addRole(RoleType.ROLE_CLIENT);

        users.add(harold);

        userRepository.save(users);


        val organisation = new Organization("Organisation 1", adminUser);
        organisation.addMember(testUser);
        organizationRepository.save(organisation);

        val category = new Category();
        category.setName("test-category");
        category.setDescription("test-category-description");
        category.setOrganization(organisation);

        categoryRepository.save(category);

        val topic = new Topic();
        topic.setName("test-topic");
        topic.setDescription("test-topic-description");
        topic.setCategory(category);

        topicRepository.save(topic);
    }
}
