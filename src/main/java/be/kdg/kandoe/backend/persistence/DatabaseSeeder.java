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


/**
 * Fills the database with seed data
 * */
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
        //seedUsersCategoriesTopicsAndSessions();
        //seedTags();
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

        User mathisse = new User();
        mathisse.setUsername("mathisse");
        mathisse.setPassword(passwordEncoder.encode("mathisse"));
        mathisse.setName("Mathisse");
        mathisse.setSurname("De Strooper");
        mathisse.setEmail("mathisse.destrooper@outlook.com");
        mathisse.setProfilePictureUrl("profilepictures/mathisse.jpg");
        mathisse.addRole(RoleType.ROLE_CLIENT);


        users.add(testUser);
        users.add(harold);
        users.add(mathisse);

        userRepository.save(users);

        Organization organization = new Organization("Karel De Grote", mathisse);
        organization.addOrganizer(harold);
        organization = organizationRepository.save(organization);

        Organization organization1 = new Organization("Government",harold);
        organization1.addOrganizer(mathisse);
        organization1 = organizationRepository.save(organization1);


        Category educationContent = new Category();
        educationContent.setOrganization(organization);
        educationContent.setName("Education content");
        educationContent.setDescription("Which content needs to be in this education?");
        educationContent = categoryRepository.save(educationContent);

        Category communication = new Category();
        communication.setOrganization(organization);
        communication.setName("Communication");
        communication.setDescription("Communication at Karel De Grote");
        communication = categoryRepository.save(communication);

        Category passport = new Category();
        passport.setOrganization(organization1);
        passport.setName("Passports, travel and living abroad");
        passport.setDescription("Includes renewing passports and travel advice by country");
        passport = categoryRepository.save(passport);

        Category money = new Category();
        money.setOrganization(organization1);
        money.setName("Money and tax");
        money.setDescription("Includes debt and Self Assessment");
        money = categoryRepository.save(money);


        Topic appliedIT = new Topic();
        appliedIT.setCategory(educationContent);
        appliedIT.setName("Applied Informatics");
        appliedIT.setDescription("What do you think is important in the eduction of IT?");
        appliedIT = topicRepository.save(appliedIT);

        Topic tax = new Topic();
        tax.setCategory(money);
        tax.setName("Self Assessment");
        tax.setDescription("What can we do to make the self assessment process easier?");
        tax = topicRepository.save(tax);

        Topic passportProcess = new Topic();
        passportProcess.setCategory(passport);
        passportProcess.setName("Apply for passport");
        passportProcess.setDescription("What can be improved during the process of applying for a passport?");
        passportProcess = topicRepository.save(tax);

        Topic media = new Topic();
        media.setCategory(communication);
        media.setName("Media");
        media.setDescription("Which type of media does KDG need to use to communicate?");
        media = topicRepository.save(media);

        seedCardDetailsKDG(educationContent, appliedIT, mathisse);
        seedCardDetailsKDG(communication, media, mathisse);
        seedCardDetailsGOV(money,tax,harold);
        seedCardDetailsGOV(passport,passportProcess,harold);

        
        /*Session session = new SynchronousSession();
        session.setOrganizer(testUser);
        session.setCategory(communication);
        session.setTopic(marketing);
        session.setAmountOfCircles(5);
        session = sessionRepository.save(session);
        
        // First session in progress
        Session sessionInProgress = new SynchronousSession();
        sessionInProgress.setOrganizer(testUser);
        sessionInProgress.setCategory(communication);
        sessionInProgress.setAmountOfCircles(5);
        sessionInProgress.setCardCommentsAllowed(false);
        sessionInProgress.setParticipantsCanAddCards(false);
        sessionInProgress.setSessionStatus(SessionStatus.IN_PROGRESS);

        ParticipantInfo participantInfoOrganizer = new ParticipantInfo();
        participantInfoOrganizer.setParticipant(testUser);

        sessionInProgress.getParticipantInfo().add(participantInfoOrganizer);
        
        Iterator<CardDetails> category1CardIterator = communication.getCards().iterator();
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
        
        sessionInProgress = sessionRepository.save(sessionInProgress);*/
        
       /* // Second session in progress
        Session sessionInProgress2 = new SynchronousSession();
        sessionInProgress2.setOrganizer(testUser);
        sessionInProgress2.setCategory(communication);
        sessionInProgress2.setAmountOfCircles(4);
        sessionInProgress2.setMinNumberOfCardsPerParticipant(2);
        sessionInProgress2.setMaxNumberOfCardsPerParticipant(6);

        sessionInProgress2.setCardCommentsAllowed(false);
        sessionInProgress2.setParticipantsCanAddCards(false);
        sessionInProgress2.setSessionStatus(SessionStatus.IN_PROGRESS);

        ParticipantInfo participantInfoOrganizer2 = new ParticipantInfo();
        participantInfoOrganizer2.setParticipant(testUser);

        sessionInProgress2.getParticipantInfo().add(participantInfoOrganizer2);
        
        category1CardIterator = communication.getCards().iterator();
        CardDetails cardsChoice2CardDetails1 = category1CardIterator.next();
        CardDetails cardsChoice2CardDetails2 = category1CardIterator.next();
        CardDetails cardsChoice2CardDetails3 = category1CardIterator.next();
        CardDetails cardsChoice2CardDetails4 = category1CardIterator.next();
        CardDetails cardsChoice2CardDetails5 = category1CardIterator.next();
        CardDetails cardsChoice2CardDetails6 = category1CardIterator.next();
        
        List<CardDetails> cardsChoice2ChosenCards = new ArrayList<>();
        *//*cardsChoice2ChosenCards.add(cardsChoice2CardDetails1);
        cardsChoice2ChosenCards.add(cardsChoice2CardDetails2);
        cardsChoice2ChosenCards.add(cardsChoice2CardDetails3);*//*
        cardsChoice2ChosenCards.addAll(communication.getCards());

        CardsChoice cardsChoice2 = new CardsChoice();
        cardsChoice2.setParticipant(testUser);
        cardsChoice2.setChosenCards(cardsChoice2ChosenCards);
        cardsChoice2.setCardsChosen(true);

        CardPosition sessionInProgress2CardPosition1 = new CardPosition(cardsChoice2CardDetails1, sessionInProgress2);
        CardPosition sessionInProgress2CardPosition2 = new CardPosition(cardsChoice2CardDetails2, sessionInProgress2);
        CardPosition sessionInProgress2CardPosition3 = new CardPosition(cardsChoice2CardDetails3, sessionInProgress2);
        CardPosition sessionInProgress2CardPosition4 = new CardPosition(cardsChoice2CardDetails4, sessionInProgress2);
        CardPosition sessionInProgress2CardPosition5 = new CardPosition(cardsChoice2CardDetails5, sessionInProgress2);
        CardPosition sessionInProgress2CardPosition6 = new CardPosition(cardsChoice2CardDetails6, sessionInProgress2);

        sessionInProgress2.getParticipantCardChoices().add(cardsChoice2);
        sessionInProgress2.getCardPositions().add(sessionInProgress2CardPosition1);
        sessionInProgress2.getCardPositions().add(sessionInProgress2CardPosition2);
        sessionInProgress2.getCardPositions().add(sessionInProgress2CardPosition3);
        sessionInProgress2.getCardPositions().add(sessionInProgress2CardPosition4);
        sessionInProgress2.getCardPositions().add(sessionInProgress2CardPosition5);
        sessionInProgress2.getCardPositions().add(sessionInProgress2CardPosition6);
        
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
        session1.setCategory(communication);
        session1.setTopic(marketing);
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
        session2.setCategory(communication);
        session2.setTopic(marketing);
        session2.setAmountOfCircles(5);

        ParticipantInfo participantInfo3 = new ParticipantInfo();
        Set<ParticipantInfo> participantInfos3 = new HashSet<>();
        participantInfo3.setParticipant(testUser);
        participantInfos3.add(participantInfo3);

        Session session3 = new SynchronousSession();
        session3.setParticipantInfo(participantInfos3);
        session3.setOrganizer(testUser);
        session3.setCategory(communication);
        session3.setTopic(marketing);
        session3.setAmountOfCircles(5);
        session3.setSessionStatus(SessionStatus.USERS_JOINING);

        sessionRepository.save(session1);
        sessionRepository.save(session2);
        sessionRepository.save(session3);*/
    }

    private Topic seedCardDetailsKDG(Category category, Topic topic, User creator) {
        if (category.getCards() == null)
            category.setCards(new HashSet<>());

        CardDetails cardDetails1 = new CardDetails();
        cardDetails1.setCategory(category);
        cardDetails1.setCreator(creator);
        cardDetails1.setText("KDG");
        cardDetails1.setImageUrl("https://pbs.twimg.com/profile_images/664027982718177280/YUs5qbQb.png");

        CardDetails cardDetails2 = new CardDetails();
        cardDetails2.setCategory(category);
        cardDetails2.setCreator(creator);
        cardDetails2.setText("ICT");
        cardDetails2.setImageUrl("http://www.themiddleeastmagazine.com/wp-content/uploads/2014/11/ICT-image.jpg");

        CardDetails cardDetails3 = new CardDetails();
        cardDetails3.setCategory(category);
        cardDetails3.setCreator(creator);
        cardDetails3.setText("Marketing");
        cardDetails3.setImageUrl("http://fortunednagroup.com/wp-content/uploads/marketing1.jpg");

        CardDetails cardDetails4 = new CardDetails();
        cardDetails4.setCategory(category);
        cardDetails4.setCreator(creator);
        cardDetails4.setText("School");
        cardDetails4.setImageUrl("http://cdn.mtlblog.com/uploads/2015/08/students-during-the-lecture.jpg");

        CardDetails cardDetails5 = new CardDetails();
        cardDetails5.setCategory(category);
        cardDetails5.setCreator(creator);
        cardDetails5.setText("Several types of media");
        cardDetails5.setImageUrl("http://www.wietblog.com/wp-content/uploads/2015/06/EU-Media-Futures-Forum-pic_0.jpg");

        CardDetails cardDetails6 = new CardDetails();
        cardDetails6.setCategory(category);
        cardDetails6.setCreator(creator);
        cardDetails6.setText("Social media");
        cardDetails6.setImageUrl("http://blogs-images.forbes.com/insider/files/2014/11/social_media_strategy111.jpg");


        cardDetails1 = cardDetailsRepository.save(cardDetails1);
        cardDetails2 = cardDetailsRepository.save(cardDetails2);
        cardDetails3 = cardDetailsRepository.save(cardDetails3);
        cardDetails4 = cardDetailsRepository.save(cardDetails4);
        cardDetails5 = cardDetailsRepository.save(cardDetails5);
        cardDetails6 = cardDetailsRepository.save(cardDetails6);

        category.getCards().add(cardDetails1);
        category.getCards().add(cardDetails2);
        category.getCards().add(cardDetails3);
        category.getCards().add(cardDetails4);
        category.getCards().add(cardDetails5);
        category.getCards().add(cardDetails6);

        category = categoryRepository.save(category);


        if (topic.getCards() == null)
            topic.setCards(new HashSet<>());

        cardDetails1.setTopics(new HashSet<>());
        cardDetails2.setTopics(new HashSet<>());
        cardDetails3.setTopics(new HashSet<>());
        cardDetails4.setTopics(new HashSet<>());
        cardDetails5.setTopics(new HashSet<>());
        cardDetails6.setTopics(new HashSet<>());

        cardDetails1.getTopics().add(topic);
        cardDetails2.getTopics().add(topic);
        cardDetails3.getTopics().add(topic);
        cardDetails4.getTopics().add(topic);
        cardDetails5.getTopics().add(topic);
        cardDetails6.getTopics().add(topic);

        cardDetails1 = cardDetailsRepository.save(cardDetails1);
        cardDetails2 = cardDetailsRepository.save(cardDetails2);
        cardDetails3 = cardDetailsRepository.save(cardDetails3);
        cardDetails4 = cardDetailsRepository.save(cardDetails4);
        cardDetails5 = cardDetailsRepository.save(cardDetails5);
        cardDetails6 = cardDetailsRepository.save(cardDetails6);

        topic.getCards().addAll(category.getCards());
        return topicRepository.save(topic);
    }

    private Topic seedCardDetailsGOV(Category category, Topic topic, User creator) {
        if (category.getCards() == null)
            category.setCards(new HashSet<>());

        CardDetails cardDetails1 = new CardDetails();
        cardDetails1.setCategory(category);
        cardDetails1.setCreator(creator);
        cardDetails1.setText("TAX");
        cardDetails1.setImageUrl("http://www.udayavani.com/sites/default/files/images/english_articles/2015/09/25/tax-600.jpg");

        CardDetails cardDetails2 = new CardDetails();
        cardDetails2.setCategory(category);
        cardDetails2.setCreator(creator);
        cardDetails2.setText("TaxOnWeb");
        cardDetails2.setImageUrl("http://www.didierreynders.be/wp-content/uploads/2011/10/Tax-on-web.jpg");


        CardDetails cardDetails4 = new CardDetails();
        cardDetails4.setCategory(category);
        cardDetails4.setCreator(creator);
        cardDetails4.setText("Passport");
        cardDetails4.setImageUrl("https://www.washington.edu/studyabroad/files/2015/02/passport.jpg");

        CardDetails cardDetails5 = new CardDetails();
        cardDetails5.setCategory(category);
        cardDetails5.setCreator(creator);
        cardDetails5.setText("Flemish government");
        cardDetails5.setImageUrl("http://kogge.be/images/uploads/2006-10-19_nieuwe_leeuw.jpg");



        cardDetails1 = cardDetailsRepository.save(cardDetails1);
        cardDetails2 = cardDetailsRepository.save(cardDetails2);
        cardDetails4 = cardDetailsRepository.save(cardDetails4);
        cardDetails5 = cardDetailsRepository.save(cardDetails5);

        category.getCards().add(cardDetails1);
        category.getCards().add(cardDetails2);
        category.getCards().add(cardDetails4);
        category.getCards().add(cardDetails5);

        category = categoryRepository.save(category);


        if (topic.getCards() == null)
            topic.setCards(new HashSet<>());

        cardDetails1.setTopics(new HashSet<>());
        cardDetails2.setTopics(new HashSet<>());
        cardDetails4.setTopics(new HashSet<>());
        cardDetails5.setTopics(new HashSet<>());

        cardDetails1.getTopics().add(topic);
        cardDetails2.getTopics().add(topic);
        cardDetails4.getTopics().add(topic);
        cardDetails5.getTopics().add(topic);

        cardDetails1 = cardDetailsRepository.save(cardDetails1);
        cardDetails2 = cardDetailsRepository.save(cardDetails2);
        cardDetails4 = cardDetailsRepository.save(cardDetails4);
        cardDetails5 = cardDetailsRepository.save(cardDetails5);

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
        tag3.setName("Media");
        Tag tag4 = new Tag();
        tagList.add(tag4);
        tag4.setName("Communication");
        Tag tag5 = new Tag();
        tagList.add(tag5);
        tag5.setName("Passport");
        Tag tag6 = new Tag();
        tagList.add(tag6);
        tag6.setName("Social");
        Tag tag7 = new Tag();
        tagList.add(tag7);
        tag7.setName("Tax");
        Tag tag8 = new Tag();
        tagList.add(tag8);
        tag8.setName("IT");
        Tag tag9 = new Tag();
        tagList.add(tag9);
        tag9.setName("Finance");
        Tag tag10 = new Tag();
        tagList.add(tag10);
        tag10.setName("School");

        tagRepository.save(tagList);
    }

}
