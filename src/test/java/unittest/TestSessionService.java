package unittest;

import be.kdg.kandoe.backend.config.BackendContextConfig;
import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.organizations.Topic;
import be.kdg.kandoe.backend.model.sessions.AsynchronousSession;
import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.backend.model.sessions.SychronusSession;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.*;
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

    User user;
    Organization organization;
    Category category;
    Topic topic;

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
        categoryService.addCategory(category, organization);
        topicService.addTopic(topic);
    }

    @Test
    public void testCreateSychronusSession(){
        SychronusSession session = new SychronusSession();

        Session savedSession = sessionService.addSession(session);
        assertEquals(session.getSessionId(), savedSession.getSessionId());
    }

    @Test
    public void testCreateAsynchronousSessionSession(){
        Session session = new AsynchronousSession();

        Session savedSession = sessionService.addSession(session);
        assertEquals(session.getSessionId(), savedSession.getSessionId());
    }

    @Test
    public void testFetchSessionsOfDifferentTypes(){
        Session asynchronousSession = new AsynchronousSession();
        Session synchronousSession = new SychronusSession();

        Session savedAsynchronousSession = sessionService.addSession(asynchronousSession);
        Session savendSynchronousSession = sessionService.addSession(synchronousSession);

        Session fetchedAsynchronousSession = sessionService.getSessionById(savedAsynchronousSession.getSessionId());
        Session fetchedSynchronousSession = sessionService.getSessionById(savendSynchronousSession.getSessionId());

        assertThat(fetchedAsynchronousSession, instanceOf(AsynchronousSession.class));
        assertThat(fetchedSynchronousSession, instanceOf(SychronusSession.class));
    }

    @Test
    public void testAddTopicToSession(){
        Session session = new SychronusSession();

        session.setTopic(this.topic);

        Session savedSession = sessionService.addSession(session);
        assertEquals(savedSession.getTopic().getName(), topic.getName());
    }
}
