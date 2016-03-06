package unittest;

import be.kdg.kandoe.backend.config.BackendContextConfig;
import be.kdg.kandoe.backend.model.cards.CardDetails;
import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.organizations.Topic;
import be.kdg.kandoe.backend.model.sessions.AsynchronousSession;
import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.backend.model.sessions.SynchronousSession;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.*;
import be.kdg.kandoe.backend.service.exceptions.SessionServiceException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { BackendContextConfig.class })
@Transactional
public class TestSessionService {
    @Autowired
    private SessionService sessionService;
    
    @Autowired
    private TopicService topicService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private CardService cardService;

    User user;
    Organization organization;
    Category category;
    Topic topic;

    private CardDetails cardDetails1;
    private CardDetails cardDetails2;
    private CardDetails cardDetails3;
    private CardDetails cardDetails4;
    private CardDetails cardDetails5;

    @Before
    public void setUp(){
        this.user = new User("testuser", "pass");
        this.organization = new Organization("test-organization", user);

        this.category = new Category();
        category.setName("test-category");
        category.setDescription("test-description");
        category.setOrganization(this.organization);


        this.topic = new Topic();
        topic.setName("test-topic");
        topic.setDescription("This is a test topic");
        topic.setCategory(category);


        userService.addUser(user);
        organizationService.addOrganization(organization);
        categoryService.addCategory(category);
        topicService.addTopic(topic);
    }

    private void addCardDetailsToCategory() {
        cardDetails1 = new CardDetails();
        cardDetails1.setCategory(category);
        cardDetails1.setCreator(user);
        cardDetails1.setText("Card 1");

        cardDetails2 = new CardDetails();
        cardDetails2.setCategory(category);
        cardDetails2.setCreator(user);
        cardDetails2.setText("Card 2");

        cardDetails3 = new CardDetails();
        cardDetails3.setCategory(category);
        cardDetails3.setCreator(user);
        cardDetails3.setText("Card 3");

        cardDetails4 = new CardDetails();
        cardDetails4.setCategory(category);
        cardDetails4.setCreator(user);
        cardDetails4.setText("Card 4");

        cardDetails5 = new CardDetails();
        cardDetails5.setCategory(category);
        cardDetails5.setCreator(user);
        cardDetails5.setText("Card 5");

        cardDetails1 = cardService.addCardDetailsToCategory(category, cardDetails1);
        cardDetails2 = cardService.addCardDetailsToCategory(category, cardDetails2);
        cardDetails3 = cardService.addCardDetailsToCategory(category, cardDetails3);
        cardDetails4 = cardService.addCardDetailsToCategory(category, cardDetails4);
        cardDetails5 = cardService.addCardDetailsToCategory(category, cardDetails5);
        
        /*cardDetails1 = cardDetailsRepository.save(cardDetails1);
        cardDetails2 = cardDetailsRepository.save(cardDetails2);
        cardDetails3 = cardDetailsRepository.save(cardDetails3);
        cardDetails4 = cardDetailsRepository.save(cardDetails4);
        cardDetails5 = cardDetailsRepository.save(cardDetails5);*/
    }

    @Test
    public void testCreateSychronusSession(){
        addCardDetailsToCategory();
        
        SynchronousSession session = new SynchronousSession();
        session.setOrganizer(user);
        session.setCategory(category);
        Session savedSession = sessionService.addSession(session);
        assertEquals(session.getSessionId(), savedSession.getSessionId());
    }

    @Test
    public void testCreateAsynchronousSessionSession(){
        addCardDetailsToCategory();
        Session session = new AsynchronousSession();
        session.setOrganizer(user);
        session.setCategory(category);
        Session savedSession = sessionService.addSession(session);
        assertEquals(session.getSessionId(), savedSession.getSessionId());
    }

    @Test
    public void testFetchSessionsOfDifferentTypes(){
        addCardDetailsToCategory();
        
        Session asynchronousSession = new AsynchronousSession();
        Session synchronousSession = new SynchronousSession();
        
        asynchronousSession.setOrganizer(user);
        asynchronousSession.setCategory(category);
        
        synchronousSession.setOrganizer(user);
        synchronousSession.setCategory(category);

        Session savedAsynchronousSession = sessionService.addSession(asynchronousSession);
        Session savedSynchronousSession = sessionService.addSession(synchronousSession);

        Session fetchedAsynchronousSession = sessionService.getSessionById(savedAsynchronousSession.getSessionId());
        Session fetchedSynchronousSession = sessionService.getSessionById(savedSynchronousSession.getSessionId());

        assertThat(fetchedAsynchronousSession, instanceOf(AsynchronousSession.class));
        assertThat(fetchedSynchronousSession, instanceOf(SynchronousSession.class));
    }

    @Test
    public void testAddSessionToTopic(){
        addCardDetailsToCategory();
        
        cardService.addCardDetailsToTopic(topic, cardDetails1);
        cardService.addCardDetailsToTopic(topic, cardDetails2);
        cardService.addCardDetailsToTopic(topic, cardDetails3);
        cardService.addCardDetailsToTopic(topic, cardDetails4);
        cardService.addCardDetailsToTopic(topic, cardDetails5);
        
        Session session = new SynchronousSession();
        session.setOrganizer(user);
        session.setCategory(category);
        session.setTopic(this.topic);

        Session savedSession = sessionService.addSession(session);
        assertEquals(savedSession.getTopic().getName(), topic.getName());
        assertEquals(savedSession.getOrganizer().getUserId(), this.user.getUserId());
    }

    //TODO fix database constraints want dat wilt niet werken
    /*
    @Test(expected = SessionServiceException.class)
    public void testAddWithoutOrganizerOfSession(){
        Session session = new SynchronousSession();
        Session savedSession = sessionService.addSession(session);
    }
    */
}
