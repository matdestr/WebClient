package integrationtest.services;

import be.kdg.kandoe.backend.config.BackendContextConfig;
import be.kdg.kandoe.backend.model.cards.CardDetails;
import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.organizations.Topic;
import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.backend.model.sessions.SynchronousSession;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.persistence.api.*;
import be.kdg.kandoe.backend.service.api.SessionService;
import be.kdg.kandoe.backend.service.exceptions.SessionServiceException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { BackendContextConfig.class })
@Transactional
public class TestSessionService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private OrganizationRepository organizationRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private TopicRepository topicRepository;
    
    @Autowired
    private CardDetailsRepository cardDetailsRepository;
    
    @Autowired
    private SessionService sessionService;
    
    private User user;
    private Organization organization;
    private Category category;
    private Topic topic;
    
    private CardDetails cardDetails1;
    private CardDetails cardDetails2;
    private CardDetails cardDetails3;
    private CardDetails cardDetails4;
    private CardDetails cardDetails5;
    
    @Before
    public void setup() {
        user = new User("test-user", "pass");
        user = userRepository.save(user);
        
        organization = new Organization("test-organization", user);
        organization = organizationRepository.save(organization);
        
        category = new Category();
        category.setOrganization(organization);
        category.setName("Category 1");
        category.setDescription("Category description");
        category = categoryRepository.save(category);
        
        topic = new Topic();
        topic.setCategory(category);
        topic.setName("Topic 1");
        topic.setDescription("Topic description");
        topic = topicRepository.save(topic);
        
        cardDetails1 = new CardDetails();
        cardDetails1.setCategory(category);
        cardDetails1.setCreator(user);
        cardDetails1.setText("Card 1");
        cardDetails1 = cardDetailsRepository.save(cardDetails1);

        cardDetails2 = new CardDetails();
        cardDetails2.setCategory(category);
        cardDetails2.setCreator(user);
        cardDetails2.setText("Card 2");
        cardDetails2 = cardDetailsRepository.save(cardDetails2);

        cardDetails3 = new CardDetails();
        cardDetails3.setCategory(category);
        cardDetails3.setCreator(user);
        cardDetails3.setText("Card 3");
        cardDetails3 = cardDetailsRepository.save(cardDetails3);

        cardDetails4 = new CardDetails();
        cardDetails4.setCategory(category);
        cardDetails4.setCreator(user);
        cardDetails4.setText("Card 4");
        cardDetails4 = cardDetailsRepository.save(cardDetails4);

        cardDetails5 = new CardDetails();
        cardDetails5.setCategory(category);
        cardDetails5.setCreator(user);
        cardDetails5.setText("Card 5");
        cardDetails5 = cardDetailsRepository.save(cardDetails5);
        
        category.setCards(new ArrayList<>());
        category.getCards().add(cardDetails1);
        category.getCards().add(cardDetails2);
        category.getCards().add(cardDetails3);
        category.getCards().add(cardDetails4);
        category.getCards().add(cardDetails5);
        
        category = categoryRepository.save(category);
    }
    
    @Test(expected = SessionServiceException.class)
    public void addSessionWithoutCategory() {
        SynchronousSession session = new SynchronousSession();
        session.setOrganizer(user);
        
        sessionService.addSession(session);
    }
    
    @Test
    public void addSessionWithLessCirclesThanMinimum() {
        Session session = new SynchronousSession();
        session.setOrganizer(user);
        session.setCategory(category);
        session.setAmountOfCircles(2);
        
        session = sessionService.addSession(session);

        Assert.assertEquals(Session.MIN_CIRCLE_AMOUNT, session.getAmountOfCircles());
    }
    
    @Test(expected = SessionServiceException.class)
    public void addSessionWithLessCardsThanRequiredPerUser() {
        Session session = new SynchronousSession();
        session.setOrganizer(user);
        session.setCategory(category);
        session.setMinNumberOfCardsPerParticipant(6);
        
        session = sessionService.addSession(session);
    }
    
    @Test(expected = SessionServiceException.class)
    public void addSessionWithLessMaxCardsThanMinCardsThrowsException() {
        Session session = new SynchronousSession();
        session.setOrganizer(user);
        session.setCategory(category);
        session.setMinNumberOfCardsPerParticipant(5);
        session.setMaxNumberOfCardsPerParticipant(4);
        
        sessionService.addSession(session);
    }
    
    @Test(expected = SessionServiceException.class)
    public void addSessionAsNonOrganizerThrowsException() {
        User nonOrganizer = new User("test-non-organizer", "pass");
        nonOrganizer = userRepository.save(nonOrganizer);
        
        Session session = new SynchronousSession();
        session.setCategory(category);
        session.setOrganizer(nonOrganizer);
        
        sessionService.addSession(session);
    }
}
