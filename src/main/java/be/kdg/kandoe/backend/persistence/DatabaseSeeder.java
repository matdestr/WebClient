package be.kdg.kandoe.backend.persistence;

import be.kdg.kandoe.backend.model.cards.CardDetails;
import be.kdg.kandoe.backend.model.cards.CardPosition;
import be.kdg.kandoe.backend.model.oauth.OAuthClientDetails;
import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.organizations.Tag;
import be.kdg.kandoe.backend.model.organizations.Topic;
import be.kdg.kandoe.backend.model.sessions.*;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.model.users.roles.RoleType;
import be.kdg.kandoe.backend.persistence.api.*;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
    
    @Autowired
    private SessionRepository sessionRepository;

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
        organization.addOrganizer(harold);
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
        
        /*for (int i = 0; i < 10; i++) {
            CardDetails cardDetails = new CardDetails();
            cardDetails.setCategory(category1);
            cardDetails.setCreator(testUser);
            cardDetails.setText("Card " + (i + 1));
            
            cardDetailsRepository.save(cardDetails);
        }*/
        
        topic1 = seedCardDetails(category1, topic1, testUser);
        
        Session session = new SynchronousSession();
        session.setOrganizer(testUser);
        session.setCategory(category1);
        session.setTopic(topic1);
        session.setAmountOfCircles(5);
        session = sessionRepository.save(session);
        
        // First session in progress
        Session sessionInProgress = new SynchronousSession();
        sessionInProgress.setOrganizer(testUser);
        sessionInProgress.setCategory(category1);
        sessionInProgress.setAmountOfCircles(5);
        sessionInProgress.setCardCommentsAllowed(false);
        sessionInProgress.setParticipantsCanAddCards(false);
        sessionInProgress.setSessionStatus(SessionStatus.IN_PROGRESS);

        ParticipantInfo participantInfoOrganizer = new ParticipantInfo();
        participantInfoOrganizer.setParticipant(testUser);

        sessionInProgress.getParticipantInfo().add(participantInfoOrganizer);
        
        Iterator<CardDetails> category1CardIterator = category1.getCards().iterator();
        CardDetails cardsChoice1CardDetails1 = category1CardIterator.next();
        CardDetails cardsChoice1CardDetails2 = category1CardIterator.next();
        
        List<CardDetails> cardsChoice1ChosenCards = new ArrayList<>();
        cardsChoice1ChosenCards.add(cardsChoice1CardDetails1);
        cardsChoice1ChosenCards.add(cardsChoice1CardDetails2);
        
        CardsChoice cardsChoice1 = new CardsChoice();
        cardsChoice1.setParticipant(testUser);
        cardsChoice1.setChosenCards(cardsChoice1ChosenCards);
        cardsChoice1.setCardsChosen(true);
        
        sessionInProgress.getParticipantCardChoices().add(cardsChoice1);
        
        sessionInProgress = sessionRepository.save(sessionInProgress);
        
        // Second session in progress
        Session sessionInProgress2 = new SynchronousSession();
        sessionInProgress2.setOrganizer(testUser);
        sessionInProgress2.setCategory(category1);
        sessionInProgress2.setAmountOfCircles(4);
        sessionInProgress2.setMinNumberOfCardsPerParticipant(2);

        sessionInProgress2.setCardCommentsAllowed(false);
        sessionInProgress2.setParticipantsCanAddCards(false);
        sessionInProgress2.setSessionStatus(SessionStatus.IN_PROGRESS);

        ParticipantInfo participantInfoOrganizer2 = new ParticipantInfo();
        participantInfoOrganizer2.setParticipant(testUser);

        sessionInProgress2.getParticipantInfo().add(participantInfoOrganizer2);
        
        category1CardIterator = category1.getCards().iterator();
        CardDetails cardsChoice2CardDetails1 = category1CardIterator.next();
        CardDetails cardsChoice2CardDetails2 = category1CardIterator.next();
        CardDetails cardsChoice2CardDetails3 = category1CardIterator.next();
        
        List<CardDetails> cardsChoice2ChosenCards = new ArrayList<>();
        cardsChoice2ChosenCards.add(cardsChoice2CardDetails1);
        cardsChoice2ChosenCards.add(cardsChoice2CardDetails2);
        cardsChoice2ChosenCards.add(cardsChoice2CardDetails3);

        CardsChoice cardsChoice2 = new CardsChoice();
        cardsChoice2.setParticipant(testUser);
        cardsChoice2.setChosenCards(cardsChoice2ChosenCards);
        cardsChoice2.setCardsChosen(true);

        CardPosition sessionInProgress2CardPosition1 = new CardPosition(cardsChoice2CardDetails1, sessionInProgress2);
        CardPosition sessionInProgress2CardPosition2 = new CardPosition(cardsChoice2CardDetails2, sessionInProgress2);
        CardPosition sessionInProgress2CardPosition3 = new CardPosition(cardsChoice2CardDetails3, sessionInProgress2);

        sessionInProgress2.getParticipantCardChoices().add(cardsChoice2);
        sessionInProgress2.getCardPositions().add(sessionInProgress2CardPosition1);
        sessionInProgress2.getCardPositions().add(sessionInProgress2CardPosition2);
        sessionInProgress2.getCardPositions().add(sessionInProgress2CardPosition3);
        
        sessionInProgress2.setCurrentParticipantPlaying(participantInfoOrganizer2);

        sessionInProgress2 = sessionRepository.save(sessionInProgress2);


        // Stuff Thanee
        ParticipantInfo participantInfo1 = new ParticipantInfo();
        Set<ParticipantInfo> participantInfos1 = new HashSet<>();
        participantInfo1.setParticipant(testUser);
        participantInfos1.add(participantInfo1);

        Session session1 = new SynchronousSession();
        session1.setParticipantInfo(participantInfos1);
        session1.setOrganizer(testUser);
        session1.setCategory(category1);
        session1.setTopic(topic1);
        session1.setAmountOfCircles(5);
        session1.setSessionStatus(SessionStatus.ADDING_CARDS);
        session1.setParticipantsCanAddCards(true);

        ParticipantInfo participantInfo2 = new ParticipantInfo();
        Set<ParticipantInfo> participantInfos2 = new HashSet<>();
        participantInfo2.setParticipant(testUser);
        participantInfos2.add(participantInfo2);

        Session session2 = new SynchronousSession();
        session2.setParticipantInfo(participantInfos2);
        session2.setOrganizer(testUser);
        session2.setCategory(category1);
        session2.setTopic(topic1);
        session2.setAmountOfCircles(5);

        ParticipantInfo participantInfo3 = new ParticipantInfo();
        Set<ParticipantInfo> participantInfos3 = new HashSet<>();
        participantInfo3.setParticipant(testUser);
        participantInfos3.add(participantInfo3);

        Session session3 = new SynchronousSession();
        session3.setParticipantInfo(participantInfos3);
        session3.setOrganizer(testUser);
        session3.setCategory(category1);
        session3.setTopic(topic1);
        session3.setAmountOfCircles(5);
        session3.setSessionStatus(SessionStatus.USERS_JOINING);

        sessionRepository.save(session1);
        sessionRepository.save(session2);
        sessionRepository.save(session3);
    }
    
    private Topic seedCardDetails(Category category, Topic topic, User creator) {
        if (category.getCards() == null)
            category.setCards(new HashSet<>());
        
        CardDetails cardDetails1 = new CardDetails();
        cardDetails1.setCategory(category);
        cardDetails1.setCreator(creator);
        cardDetails1.setText("Card #1");
        cardDetails1.setImageUrl("https://s-media-cache-ak0.pinimg.com/736x/41/99/ed/4199edcef653e72fa3dd9b9bb629f2f5.jpg");
        
        CardDetails cardDetails2 = new CardDetails();
        cardDetails2.setCategory(category);
        cardDetails2.setCreator(creator);
        cardDetails2.setText("Card #2");
        cardDetails2.setImageUrl("http://figures.boundless.com/11036/full/cash.jpeg");
        
        CardDetails cardDetails3 = new CardDetails();
        cardDetails3.setCategory(category);
        cardDetails3.setCreator(creator);
        cardDetails3.setText("Harry the cactus");
        cardDetails3.setImageUrl("http://i4.mirror.co.uk/incoming/article5704312.ece/ALTERNATES/s615b/waving-cactus.jpg");
        
        cardDetails1 = cardDetailsRepository.save(cardDetails1);
        cardDetails2 = cardDetailsRepository.save(cardDetails2);
        cardDetails3 = cardDetailsRepository.save(cardDetails3);
        
        category.getCards().add(cardDetails1);
        category.getCards().add(cardDetails2);
        category.getCards().add(cardDetails3);
        
        category = categoryRepository.save(category);
        
        if (topic.getCards() == null)
            topic.setCards(new HashSet<>());

        cardDetails1.setTopics(new HashSet<>());
        cardDetails2.setTopics(new HashSet<>());
        cardDetails3.setTopics(new HashSet<>());
        
        cardDetails1.getTopics().add(topic);
        cardDetails2.getTopics().add(topic);
        cardDetails3.getTopics().add(topic);

        cardDetails1 = cardDetailsRepository.save(cardDetails1);
        cardDetails2 = cardDetailsRepository.save(cardDetails2);
        cardDetails3 = cardDetailsRepository.save(cardDetails3);
        
        topic.getCards().addAll(category.getCards());
        return topicRepository.save(topic);
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
    
    /*
        // TODO remove !!
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
    }*/
}
