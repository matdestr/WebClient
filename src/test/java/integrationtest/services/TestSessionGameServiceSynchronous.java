package integrationtest.services;

import be.kdg.kandoe.backend.config.BackendContextConfig;
import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.sessions.ParticipantInfo;
import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.backend.model.sessions.SessionStatus;
import be.kdg.kandoe.backend.model.sessions.SynchronousSession;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.persistence.api.CategoryRepository;
import be.kdg.kandoe.backend.persistence.api.OrganizationRepository;
import be.kdg.kandoe.backend.persistence.api.SessionRepository;
import be.kdg.kandoe.backend.service.api.EmailService;
import be.kdg.kandoe.backend.service.api.SessionGameService;
import be.kdg.kandoe.backend.service.api.SessionService;
import be.kdg.kandoe.backend.service.api.UserService;
import be.kdg.kandoe.backend.service.exceptions.SessionGameServiceException;
import be.kdg.kandoe.backend.service.impl.SessionGameServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { BackendContextConfig.class })
@Transactional
public class TestSessionGameServiceSynchronous {
    @Autowired
    private UserService userService;
    
    @Autowired
    private OrganizationRepository organizationRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private SessionRepository sessionRepository;
    
    @Autowired
    private SessionService sessionService;
    
    /*@Autowired
    private EmailService emailService;*/
    
    @Autowired
    private SessionGameService sessionGameService;
    
    private User organizer;
    private User player1;
    private User player2;
    private User player3;
    
    private Session sessionCreated;
    private Session sessionUsersJoining;
    private Session sessionAddingCards;
    private Session sessionReviewingCards;
    private Session sessionInProgress;
    private Session sessionFinished;
    
    @Before
    public void initializeSessions() {
        User user = new User("test-user", "test-pass");
        organizer = userService.addUser(user);
        
        player1 = userService.addUser(
                new User("test-player-1", "test-pass")
        );

        player2 = userService.addUser(
                new User("test-player-2", "test-pass")
        );

        player3 = userService.addUser(
                new User("test-player-3", "test-pass")
        );
        
        Organization organization = new Organization("test-organization", user);
        organization = organizationRepository.save(organization);

        Category category = new Category();
        category.setOrganization(organization);
        category.setName("test-category");
        category.setDescription("test-category-description");
        category = categoryRepository.save(category);
        
        sessionCreated = new SynchronousSession();
        sessionCreated.setCategory(category);
        sessionCreated.setOrganizer(user);
        sessionCreated.setCardCommentsAllowed(true);
        ParticipantInfo sessionCreatedOrganizerParticipantInfo = new ParticipantInfo();
        sessionCreatedOrganizerParticipantInfo.setParticipant(organizer);
        sessionCreated.getParticipantInfo().add(sessionCreatedOrganizerParticipantInfo);
        sessionCreated = sessionRepository.save(sessionCreated);
        //sessionCreated = sessionService.addSession(sessionCreated);
        
        sessionAddingCards = new SynchronousSession();
        sessionAddingCards.setCategory(category);
        sessionAddingCards.setOrganizer(user);
        sessionAddingCards.setSessionStatus(SessionStatus.ADDING_CARDS);
        ParticipantInfo sessionAddingCardsOrganizerParticipantInfo = new ParticipantInfo();
        sessionAddingCardsOrganizerParticipantInfo.setParticipant(organizer);
        sessionAddingCards.getParticipantInfo().add(sessionAddingCardsOrganizerParticipantInfo);
        sessionAddingCards = sessionRepository.save(sessionAddingCards);
        //sessionAddingCards = sessionService.addSession(sessionAddingCards);
    }
    
    @Test
    public void inviteUsersAndJoin() {
        EmailService mockedEmailService = Mockito.mock(EmailService.class);
        sessionGameService = new SessionGameServiceImpl(sessionRepository, mockedEmailService);
        
        sessionGameService.inviteUserForSession(sessionCreated, player1);
        Assert.assertEquals(SessionStatus.USERS_JOINING, sessionCreated.getSessionStatus());
        Assert.assertFalse(sessionCreated.getParticipantInfo().iterator().next().isJoined());
        Mockito.verify(mockedEmailService).sendSessionInvitationToUser(
                Mockito.eq(sessionCreated), Mockito.eq(organizer), Mockito.eq(player1)
        );
        
        sessionGameService.setUserJoined(sessionCreated, organizer);
        sessionGameService.setUserJoined(sessionCreated, player1);
        Assert.assertEquals(2, sessionCreated.getParticipantInfo().size());
        Assert.assertTrue(sessionCreated.getParticipantInfo().stream().allMatch(p -> p.isJoined()));
    }
    
    @Test(expected = SessionGameServiceException.class)
    public void inviteUsersWhenGameHasStarted() {
        sessionGameService.inviteUserForSession(sessionAddingCards, player1);
    }
    
    @Test(expected = SessionGameServiceException.class)
    public void joinAsNonInvitedUser() {
        sessionGameService.setUserJoined(sessionCreated, player1);
    }
    
    @Test
    public void inviteTwoUsersAndLetOneJoinKeepsUsersJoiningStatus() {
        EmailService mockedEmailService = Mockito.mock(EmailService.class);
        sessionGameService = new SessionGameServiceImpl(sessionRepository, mockedEmailService);

        sessionGameService.inviteUserForSession(sessionCreated, player1);
        sessionGameService.inviteUserForSession(sessionCreated, player2);
        
        sessionGameService.setUserJoined(sessionCreated, organizer);
        sessionGameService.setUserJoined(sessionCreated, player1);
        Assert.assertEquals(SessionStatus.USERS_JOINING, sessionCreated.getSessionStatus());
        sessionGameService.setUserJoined(sessionCreated, player2);
        Assert.assertEquals(SessionStatus.REVIEWING_CARDS, sessionCreated.getSessionStatus());
    }
    
    @Test
    public void joinAllUsersWithAddCardsAllowedResultsInAddCardsSessionStatus() {
        sessionCreated.setParticipantsCanAddCards(true);
        sessionCreated = this.sessionRepository.save(sessionCreated);
        
        EmailService mockedEmailService = Mockito.mock(EmailService.class);
        sessionGameService = new SessionGameServiceImpl(sessionRepository, mockedEmailService);

        sessionGameService.inviteUserForSession(sessionCreated, player1);
        sessionGameService.inviteUserForSession(sessionCreated, player2);
        
        sessionGameService.setUserJoined(sessionCreated, organizer);
        sessionGameService.setUserJoined(sessionCreated, player1);
        Assert.assertEquals(SessionStatus.USERS_JOINING, sessionCreated.getSessionStatus());
        sessionGameService.setUserJoined(sessionCreated, player2);
        Assert.assertEquals(SessionStatus.ADDING_CARDS, sessionCreated.getSessionStatus());
    }
    
    @Test
    public void joinAllUsersWithNothingAllowedResultsInSessionStatusInProgress() {
        EmailService mockedEmailService = Mockito.mock(EmailService.class);
        sessionGameService = new SessionGameServiceImpl(sessionRepository, mockedEmailService);
        
        sessionCreated.setParticipantsCanAddCards(false);
        sessionCreated.setCardCommentsAllowed(false);
        sessionCreated = sessionRepository.save(sessionCreated);
        
        this.inviteTwoUsersAndLetThemJoin(sessionCreated);
        sessionGameService.setUserJoined(sessionCreated, organizer);
        Assert.assertEquals(SessionStatus.IN_PROGRESS, sessionCreated.getSessionStatus());
    }
    
    private void inviteTwoUsersAndLetThemJoin(Session session) {
        sessionGameService.inviteUserForSession(session, player1);
        sessionGameService.inviteUserForSession(session, player2);
        
        sessionGameService.setUserJoined(session, player1);
        sessionGameService.setUserJoined(session, player2);
    }
}
