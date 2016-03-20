package integrationtest.services;

import be.kdg.kandoe.backend.config.BackendContextConfig;
import be.kdg.kandoe.backend.model.cards.CardDetails;
import be.kdg.kandoe.backend.model.cards.CardPosition;
import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.sessions.*;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.*;
import be.kdg.kandoe.backend.service.exceptions.SessionGameServiceException;
import be.kdg.kandoe.backend.service.exceptions.SessionServiceException;
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

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {BackendContextConfig.class})
@Transactional
public class TestSessionGameServiceAsynchronous {
    @Autowired
    private UserService userService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CardService cardService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private SessionGameService sessionGameService;

    private User organizer;
    private User player1;
    private User player2;
    private User player3;

    private Session session;
    private CardDetails cardDetails1;
    private CardDetails cardDetails2;
    private CardDetails cardDetails3;
    private CardDetails cardDetails4;
    private CardDetails cardDetails5;
    private Category category;
    private User user;


    @Before
    public void initializeSessions() {
        organizer = new User("test-organizer", "test-pass");
        organizer.setEmail("organizer@localhost");
        organizer = userService.addUser(organizer);
        user = organizer;

        player1 = new User("test-player-1", "test-pass");
        player1.setEmail("player1@localhost");
        player1 = userService.addUser(player1);

        player2 = new User("test-player-2", "test-pass");
        player2.setEmail("player2@localhost");
        player2 = userService.addUser(player2);

        player3 = new User("test-player-3", "test-pass");
        player3.setEmail("player3@localhost");
        player3 = userService.addUser(player3);

        Organization organization = new Organization("test-organization", user);
        organization = organizationService.addOrganization(organization);


        category = new Category();
        category.setOrganization(organization);
        category.setName("test-category");
        category.setDescription("test-category-description");
        category = categoryService.addCategory(category);

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


        AsynchronousSession session = new AsynchronousSession();
        session.setSecondsBetweenMoves(10);
        session.setCategory(category);
        session.setOrganizer(user);
        session.setCardCommentsAllowed(true);
        ParticipantInfo sessionCreatedOrganizerParticipantInfo = new ParticipantInfo();
        sessionCreatedOrganizerParticipantInfo.setParticipant(organizer);
        session.getParticipantInfo().add(sessionCreatedOrganizerParticipantInfo);
        session.setMinNumberOfCardsPerParticipant(1);
        session.setMaxNumberOfCardsPerParticipant(3);
        this.session = sessionService.addSession(session);
    }

    @Test
    public void inviteUsersAndJoin() {
        EmailService mockedEmailService = Mockito.mock(EmailService.class);
        sessionGameService = new SessionGameServiceImpl(sessionService, mockedEmailService);

        sessionGameService.inviteUserForSession(session, player1);
        sessionGameService.confirmInvitedUsers(session);

        Assert.assertEquals(SessionStatus.USERS_JOINING, session.getSessionStatus());
        Assert.assertFalse(session.getParticipantInfo().iterator().next().isJoined());
        Mockito.verify(mockedEmailService).sendSessionInvitationToUser(
                Mockito.eq(session), Mockito.eq(organizer), Mockito.eq(player1)
        );

        sessionGameService.setUserJoined(session, organizer);
        sessionGameService.setUserJoined(session, player1);
        Assert.assertEquals(2, session.getParticipantInfo().size());
        Assert.assertTrue(session.getParticipantInfo().stream().allMatch(p -> p.isJoined()));
    }

    @Test(expected = SessionServiceException.class)
    public void testSessionWithTimesBetweenMovesLessThanZero(){
        AsynchronousSession asynchronousSession = new AsynchronousSession();
        asynchronousSession.setSecondsBetweenMoves(-1);
        asynchronousSession.setCategory(category);
        asynchronousSession.setOrganizer(user);
        asynchronousSession.setCardCommentsAllowed(true);
        ParticipantInfo sessionCreatedOrganizerParticipantInfo = new ParticipantInfo();
        sessionCreatedOrganizerParticipantInfo.setParticipant(organizer);
        asynchronousSession.getParticipantInfo().add(sessionCreatedOrganizerParticipantInfo);
        asynchronousSession.setMinNumberOfCardsPerParticipant(1);
        asynchronousSession.setMaxNumberOfCardsPerParticipant(3);
        sessionService.addSession(asynchronousSession);
    }

    @Test(expected = SessionGameServiceException.class)
    public void inviteUsersWhenGameIsNotInInvitingStatus() {
        session.setSessionStatus(SessionStatus.ADDING_CARDS);
        sessionGameService.inviteUserForSession(session, player1);
    }

    @Test(expected = SessionGameServiceException.class)
    public void joinAsNonInvitedUserWithoutConfirmedInvites() {
        sessionGameService.setUserJoined(session, player1);
    }

    @Test(expected = SessionGameServiceException.class)
    public void joinAsNonInvitedUserWithConfirmedInvites() {
        sessionGameService.confirmInvitedUsers(session);
        sessionGameService.setUserJoined(session, player1);
    }

    @Test
    public void inviteTwoUsersAndLetOneJoinKeepsUsersJoiningStatus() {
        EmailService mockedEmailService = Mockito.mock(EmailService.class);
        sessionGameService = new SessionGameServiceImpl(sessionService, mockedEmailService);

        sessionGameService.inviteUserForSession(session, player1);
        sessionGameService.inviteUserForSession(session, player2);

        Assert.assertEquals(SessionStatus.CREATED, session.getSessionStatus());

        sessionGameService.confirmInvitedUsers(session);

        sessionGameService.setUserJoined(session, organizer);
        sessionGameService.setUserJoined(session, player1);
        Assert.assertEquals(SessionStatus.USERS_JOINING, session.getSessionStatus());
        sessionGameService.setUserJoined(session, player2);
        Assert.assertEquals(SessionStatus.REVIEWING_CARDS, session.getSessionStatus());
    }

    @Test(expected = SessionGameServiceException.class)
    public void inviteUserAfterConfirmingInvitesThrowsException() {
        sessionGameService.inviteUserForSession(session, player1);
        Assert.assertEquals(SessionStatus.CREATED, session.getSessionStatus());

        sessionGameService.inviteUserForSession(session, player2);
        Assert.assertEquals(SessionStatus.CREATED, session.getSessionStatus());

        sessionGameService.confirmInvitedUsers(session);
        Assert.assertEquals(SessionStatus.USERS_JOINING, session.getSessionStatus());

        sessionGameService.inviteUserForSession(session, player3);
    }

    @Test(expected = SessionGameServiceException.class)
    public void confirmInvitedUsersMultipleTimesThrowsException() {
        sessionGameService.inviteUserForSession(session, player1);
        Assert.assertEquals(SessionStatus.CREATED, session.getSessionStatus());

        sessionGameService.confirmInvitedUsers(session);
        sessionGameService.confirmInvitedUsers(session);
    }

    @Test(expected = SessionGameServiceException.class)
    public void joinSessionAsInvitedUserWhenNotConfirmedThrowsException() {
        sessionGameService.inviteUserForSession(session, player1);
        sessionGameService.setUserJoined(session, player1);
    }

    @Test
    public void joinAllUsersWithAddCardsAllowedResultsInAddCardsSessionStatus() {
        session.setParticipantsCanAddCards(true);
        session = this.sessionService.updateSession(session);

        EmailService mockedEmailService = Mockito.mock(EmailService.class);
        sessionGameService = new SessionGameServiceImpl(sessionService, mockedEmailService);

        sessionGameService.inviteUserForSession(session, player1);
        sessionGameService.inviteUserForSession(session, player2);
        sessionGameService.confirmInvitedUsers(session);

        sessionGameService.setUserJoined(session, organizer);
        sessionGameService.setUserJoined(session, player1);
        Assert.assertEquals(SessionStatus.USERS_JOINING, session.getSessionStatus());
        sessionGameService.setUserJoined(session, player2);
        Assert.assertEquals(SessionStatus.ADDING_CARDS, session.getSessionStatus());
    }

    @Test
    public void joinAllUsersWithNothingAllowedResultsInSessionStatusChoosingCards() {
        EmailService mockedEmailService = Mockito.mock(EmailService.class);
        sessionGameService = new SessionGameServiceImpl(sessionService, mockedEmailService);

        session.setParticipantsCanAddCards(false);
        session.setCardCommentsAllowed(false);
        session = sessionService.updateSession(session);

        this.inviteTwoUsersAndLetThemJoin(session);
        sessionGameService.setUserJoined(session, organizer);
        Assert.assertEquals(SessionStatus.CHOOSING_CARDS, session.getSessionStatus());
    }

    @Test
    public void chooseCardsAndConfirm() {
        session.setParticipantsCanAddCards(true);
        session.setCardCommentsAllowed(false);
        session = sessionService.updateSession(session);

        this.inviteTwoUsersAndLetThemJoin(session);
        sessionGameService.setUserJoined(session, organizer);
        Assert.assertTrue(session.isParticipantsCanAddCards());
        Assert.assertEquals(SessionStatus.ADDING_CARDS, session.getSessionStatus());

        sessionGameService.confirmAddedCards(session);
        Assert.assertEquals(SessionStatus.CHOOSING_CARDS, session.getSessionStatus());

        Set<CardDetails> cardDetailsSet = new HashSet<>();
        cardDetailsSet.add(cardDetails3);

        sessionGameService.chooseCards(session, organizer, cardDetailsSet);
        //sessionGameService.confirmCardsChosen(session, organizer);

        cardDetailsSet = new HashSet<>();
        cardDetailsSet.add(cardDetails1);

        sessionGameService.chooseCards(session, player1, cardDetailsSet);
        //sessionGameService.confirmCardsChosen(session, player1);

        cardDetailsSet = new HashSet<>();
        cardDetailsSet.add(cardDetails1);
        cardDetailsSet.add(cardDetails2);

        sessionGameService.chooseCards(session, player2, cardDetailsSet);
        //sessionGameService.confirmCardsChosen(session, player2);

        Assert.assertEquals(SessionStatus.READY_TO_START, session.getSessionStatus());
    }

    @Test
    public void chooseLessCardsThanAllowed() {
        session.setParticipantsCanAddCards(true);
        session.setCardCommentsAllowed(false);
        session = sessionService.updateSession(session);

        this.inviteTwoUsersAndLetThemJoin(session);
        sessionGameService.setUserJoined(session, organizer);
        Assert.assertTrue(session.isParticipantsCanAddCards());
        Assert.assertEquals(SessionStatus.ADDING_CARDS, session.getSessionStatus());

        sessionGameService.confirmAddedCards(session);
        Assert.assertEquals(SessionStatus.CHOOSING_CARDS, session.getSessionStatus());

        Set<CardDetails> cardDetailsSet = new HashSet<>();
        cardDetailsSet.add(cardDetails3);

        sessionGameService.chooseCards(session, organizer, cardDetailsSet);
        //sessionGameService.confirmCardsChosen(session, organizer);

        cardDetailsSet = new HashSet<>();
        cardDetailsSet.add(cardDetails1);

        //sessionGameService.chooseCards(session, player1, cardDetailsSet);
        //sessionGameService.confirmCardsChosen(session, player1);

        //sessionGameService.confirmCardsChosen(session, player2);

        session = sessionService.getSessionById(session.getSessionId());
        Assert.assertEquals(SessionStatus.CHOOSING_CARDS, session.getSessionStatus());
    }

    @Test(expected = SessionGameServiceException.class)
    public void chooseMoreCardsThanAllowed() {
        session.setParticipantsCanAddCards(true);
        session.setCardCommentsAllowed(false);
        session = sessionService.updateSession(session);

        this.inviteTwoUsersAndLetThemJoin(session);
        sessionGameService.setUserJoined(session, organizer);
        Assert.assertTrue(session.isParticipantsCanAddCards());
        Assert.assertEquals(SessionStatus.ADDING_CARDS, session.getSessionStatus());

        sessionGameService.confirmAddedCards(session);
        Assert.assertEquals(SessionStatus.CHOOSING_CARDS, session.getSessionStatus());

        Set<CardDetails> cardDetailsSet = new HashSet<>();
        cardDetailsSet.add(cardDetails3);

        sessionGameService.chooseCards(session, organizer, cardDetailsSet);
        //sessionGameService.confirmCardsChosen(session, organizer);

        cardDetailsSet = new HashSet<>();
        cardDetailsSet.add(cardDetails1);

        sessionGameService.chooseCards(session, player1, cardDetailsSet);

        cardDetailsSet = new HashSet<>();
        cardDetailsSet.add(cardDetails2);

        sessionGameService.chooseCards(session, player1, cardDetailsSet);

        cardDetailsSet = new HashSet<>();
        cardDetailsSet.add(cardDetails4);

        sessionGameService.chooseCards(session, player1, cardDetailsSet);


        cardDetailsSet = new HashSet<>();
        cardDetailsSet.add(cardDetails3);

        sessionGameService.chooseCards(session, player1, cardDetailsSet);

        cardDetailsSet = new HashSet<>();
        cardDetailsSet.add(cardDetails5);

        sessionGameService.chooseCards(session, player1, cardDetailsSet);
        //sessionGameService.confirmCardsChosen(session, player1);
    }

    @Test
    public void uniqueCardsPositions() {
        session.setParticipantsCanAddCards(true);
        session.setCardCommentsAllowed(false);
        session = sessionService.updateSession(session);

        this.inviteTwoUsersAndLetThemJoin(session);
        sessionGameService.setUserJoined(session, organizer);
        Assert.assertTrue(session.isParticipantsCanAddCards());
        Assert.assertEquals(SessionStatus.ADDING_CARDS, session.getSessionStatus());

        sessionGameService.confirmAddedCards(session);
        Assert.assertEquals(SessionStatus.CHOOSING_CARDS, session.getSessionStatus());

        Set<CardDetails> cardDetailsSet = new HashSet<>();
        cardDetailsSet.add(cardDetails3);

        sessionGameService.chooseCards(session, organizer, cardDetailsSet);

        cardDetailsSet = new HashSet<>();
        cardDetailsSet.add(cardDetails1);
        cardDetailsSet.add(cardDetails2);

        sessionGameService.chooseCards(session, player1, cardDetailsSet);

        sessionGameService.chooseCards(session, player2, cardDetailsSet);

        Assert.assertEquals(SessionStatus.READY_TO_START, session.getSessionStatus());
        List<CardPosition> cardPositionList = session.getCardPositions();
        Comparator comparator = new Comparator<CardPosition>() {
            @Override
            public int compare(CardPosition o1, CardPosition o2) {
                return Integer.compare(o1.getCardPositionId(), o2.getCardPositionId());
            }
        };
        Collections.sort(cardPositionList, comparator);

        TreeSet set = new TreeSet(comparator);
        set.addAll(cardPositionList);

        Assert.assertArrayEquals(cardPositionList.toArray(), set.toArray());
    }

    @Test
    public void testStartGame() {
        session.setParticipantsCanAddCards(true);
        session.setCardCommentsAllowed(false);
        session = sessionService.updateSession(session);

        this.inviteTwoUsersAndLetThemJoin(session);
        sessionGameService.setUserJoined(session, organizer);
        Assert.assertTrue(session.isParticipantsCanAddCards());
        Assert.assertEquals(SessionStatus.ADDING_CARDS, session.getSessionStatus());

        sessionGameService.confirmAddedCards(session);
        Assert.assertEquals(SessionStatus.CHOOSING_CARDS, session.getSessionStatus());

        Set<CardDetails> cardDetailsSet = new HashSet<>();
        cardDetailsSet.add(cardDetails3);

        sessionGameService.chooseCards(session, organizer, cardDetailsSet);
        //sessionGameService.confirmCardsChosen(session, organizer);

        cardDetailsSet = new HashSet<>();
        cardDetailsSet.add(cardDetails1);
        cardDetailsSet.add(cardDetails2);

        sessionGameService.chooseCards(session, player1, cardDetailsSet);
        //sessionGameService.confirmCardsChosen(session, player1);

        cardDetailsSet = new HashSet<>();
        cardDetailsSet.add(cardDetails1);
        cardDetailsSet.add(cardDetails2);

        sessionGameService.chooseCards(session, player2, cardDetailsSet);
        //sessionGameService.confirmCardsChosen(session, player2);

        Assert.assertEquals(SessionStatus.READY_TO_START, session.getSessionStatus());
        List<CardPosition> cardPositionList = session.getCardPositions();
        Comparator comparator = new Comparator<CardPosition>() {
            @Override
            public int compare(CardPosition o1, CardPosition o2) {
                return Integer.compare(o1.getCardPositionId(), o2.getCardPositionId());
            }
        };
        Collections.sort(cardPositionList, comparator);

        TreeSet set = new TreeSet(comparator);
        set.addAll(cardPositionList);

        Assert.assertArrayEquals(cardPositionList.toArray(), set.toArray());

        sessionGameService.startGame(session);
        Assert.assertEquals(SessionStatus.IN_PROGRESS, session.getSessionStatus());
    }

    @Test
    public void testIncreaseCardPriority() {
        session.setParticipantsCanAddCards(true);
        session.setCardCommentsAllowed(false);
        session = sessionService.updateSession(session);

        this.inviteTwoUsersAndLetThemJoin(session);
        sessionGameService.setUserJoined(session, organizer);
        Assert.assertTrue(session.isParticipantsCanAddCards());
        Assert.assertEquals(SessionStatus.ADDING_CARDS, session.getSessionStatus());

        sessionGameService.confirmAddedCards(session);
        Assert.assertEquals(SessionStatus.CHOOSING_CARDS, session.getSessionStatus());

        Set<CardDetails> cardDetailsSet = new HashSet<>();
        cardDetailsSet.add(cardDetails1);

        sessionGameService.chooseCards(session, player1, cardDetailsSet);

        cardDetailsSet = new HashSet<>();
        cardDetailsSet.add(cardDetails1);
        cardDetailsSet.add(cardDetails2);

        sessionGameService.chooseCards(session, player2, cardDetailsSet);

        cardDetailsSet = new HashSet<>();
        cardDetailsSet.add(cardDetails3);

        sessionGameService.chooseCards(session, organizer, cardDetailsSet);

        Assert.assertEquals(SessionStatus.READY_TO_START, session.getSessionStatus());
        List<CardPosition> cardPositionList = session.getCardPositions();
        Comparator comparator = new Comparator<CardPosition>() {
            @Override
            public int compare(CardPosition o1, CardPosition o2) {
                return Integer.compare(o1.getCardPositionId(), o2.getCardPositionId());
            }
        };
        Collections.sort(cardPositionList, comparator);

        TreeSet set = new TreeSet(comparator);
        set.addAll(cardPositionList);

        Assert.assertArrayEquals(cardPositionList.toArray(), set.toArray());

        sessionGameService.startGame(session);
        Assert.assertEquals(SessionStatus.IN_PROGRESS, session.getSessionStatus());

        sessionGameService.increaseCardPriority(session, player1, cardDetails1);

        cardPositionList = session.getCardPositions();
        CardPosition cardPositionOfCard1 = cardPositionList.stream().filter(c -> c.getCardDetails().getCardDetailsId() == cardDetails1.getCardDetailsId()).findFirst().get();
        Assert.assertEquals(2, cardPositionOfCard1.getPriority());
    }

    @Test
    public void testParticipantsSequence() {
        session.setParticipantsCanAddCards(false);
        session.setCardCommentsAllowed(false);

        session = sessionService.updateSession(session);
        this.inviteTwoUsersAndLetThemJoin(session);
        sessionGameService.setUserJoined(session, organizer);

        Assert.assertEquals(SessionStatus.CHOOSING_CARDS, session.getSessionStatus());

        Set<CardDetails> cardDetailsSet = new HashSet<>();
        cardDetailsSet.add(cardDetails3);

        sessionGameService.chooseCards(session, organizer, cardDetailsSet);
        //sessionGameService.confirmCardsChosen(session, organizer);

        cardDetailsSet = new HashSet<>();
        cardDetailsSet.add(cardDetails1);
        cardDetailsSet.add(cardDetails2);

        sessionGameService.chooseCards(session, player1, cardDetailsSet);

        cardDetailsSet = new HashSet<>();
        cardDetailsSet.add(cardDetails1);
        cardDetailsSet.add(cardDetails2);

        sessionGameService.chooseCards(session, player2, cardDetailsSet);
        ;

        Assert.assertEquals(SessionStatus.READY_TO_START, session.getSessionStatus());

        sessionGameService.startGame(session);

        List<ParticipantInfo> participantSequence = session.getParticipantSequence();

        Assert.assertEquals(session.getCurrentParticipantPlaying().getParticipant().getUserId(), participantSequence.get(0).getParticipant().getUserId());
        Assert.assertEquals(SessionStatus.IN_PROGRESS, session.getSessionStatus());

        sessionGameService.increaseCardPriority(session, player1, cardDetails1);

        Assert.assertEquals(session.getCurrentParticipantPlaying().getParticipant().getUserId(), participantSequence.get(1).getParticipant().getUserId());
        Assert.assertEquals(SessionStatus.IN_PROGRESS, session.getSessionStatus());

        sessionGameService.increaseCardPriority(session, player2, cardDetails1);

        Assert.assertEquals(session.getCurrentParticipantPlaying().getParticipant().getUserId(), participantSequence.get(2).getParticipant().getUserId());
        Assert.assertEquals(SessionStatus.IN_PROGRESS, session.getSessionStatus());

        sessionGameService.increaseCardPriority(session, user, cardDetails1);

        Assert.assertEquals(session.getCurrentParticipantPlaying().getParticipant().getUserId(), participantSequence.get(0).getParticipant().getUserId());
        Assert.assertEquals(SessionStatus.IN_PROGRESS, session.getSessionStatus());
    }

    @Test
    public void testEndGame() {
        session.setParticipantsCanAddCards(false);
        session.setCardCommentsAllowed(false);
        session.setAmountOfCircles(4);

        session = sessionService.updateSession(session);
        this.inviteTwoUsersAndLetThemJoin(session);
        sessionGameService.setUserJoined(session, organizer);

        Assert.assertEquals(SessionStatus.CHOOSING_CARDS, session.getSessionStatus());

        Set<CardDetails> cardDetailsSet = new HashSet<>();
        cardDetailsSet.add(cardDetails3);

        sessionGameService.chooseCards(session, organizer, cardDetailsSet);

        cardDetailsSet = new HashSet<>();
        cardDetailsSet.add(cardDetails1);
        cardDetailsSet.add(cardDetails2);

        sessionGameService.chooseCards(session, player1, cardDetailsSet);

        cardDetailsSet = new HashSet<>();
        cardDetailsSet.add(cardDetails1);
        cardDetailsSet.add(cardDetails2);

        sessionGameService.chooseCards(session, player2, cardDetailsSet);

        Assert.assertEquals(SessionStatus.READY_TO_START, session.getSessionStatus());

        sessionGameService.startGame(session);

        List<ParticipantInfo> participantSequence = session.getParticipantSequence();

        Assert.assertEquals(session.getCurrentParticipantPlaying().getParticipant().getUserId(), participantSequence.get(0).getParticipant().getUserId());
        Assert.assertEquals(SessionStatus.IN_PROGRESS, session.getSessionStatus());

        sessionGameService.increaseCardPriority(session, player1, cardDetails1);

        Assert.assertEquals(session.getCurrentParticipantPlaying().getParticipant().getUserId(), participantSequence.get(1).getParticipant().getUserId());
        Assert.assertEquals(SessionStatus.IN_PROGRESS, session.getSessionStatus());

        sessionGameService.increaseCardPriority(session, player2, cardDetails1);

        Assert.assertEquals(session.getCurrentParticipantPlaying().getParticipant().getUserId(), participantSequence.get(2).getParticipant().getUserId());
        Assert.assertEquals(SessionStatus.IN_PROGRESS, session.getSessionStatus());

        sessionGameService.increaseCardPriority(session, user, cardDetails1);

        Assert.assertEquals(1, session.getWinners().size());
        Assert.assertTrue(session.getWinners().contains(cardDetails1));
        Assert.assertEquals(SessionStatus.FINISHED, session.getSessionStatus());
    }

    @Test
    public void testForceEndGame() {
        session.setParticipantsCanAddCards(false);
        session.setCardCommentsAllowed(false);
        session.setAmountOfCircles(3);

        session = sessionService.updateSession(session);
        this.inviteTwoUsersAndLetThemJoin(session);
        sessionGameService.setUserJoined(session, organizer);

        Assert.assertEquals(SessionStatus.CHOOSING_CARDS, session.getSessionStatus());

        Set<CardDetails> cardDetailsSet = new HashSet<>();
        cardDetailsSet.add(cardDetails3);

        sessionGameService.chooseCards(session, organizer, cardDetailsSet);

        cardDetailsSet = new HashSet<>();
        cardDetailsSet.add(cardDetails1);
        cardDetailsSet.add(cardDetails2);

        sessionGameService.chooseCards(session, player1, cardDetailsSet);

        cardDetailsSet = new HashSet<>();
        cardDetailsSet.add(cardDetails1);
        cardDetailsSet.add(cardDetails2);

        sessionGameService.chooseCards(session, player2, cardDetailsSet);
        ;

        Assert.assertEquals(SessionStatus.READY_TO_START, session.getSessionStatus());

        sessionGameService.startGame(session);

        List<ParticipantInfo> participantSequence = session.getParticipantSequence();

        Assert.assertEquals(session.getCurrentParticipantPlaying().getParticipant().getUserId(), participantSequence.get(0).getParticipant().getUserId());
        Assert.assertEquals(SessionStatus.IN_PROGRESS, session.getSessionStatus());

        sessionGameService.increaseCardPriority(session, player1, cardDetails1);

        sessionGameService.endGame(session);

        Assert.assertEquals(1, session.getWinners().size());
        Assert.assertTrue(session.getWinners().contains(cardDetails1));
        Assert.assertEquals(SessionStatus.FINISHED, session.getSessionStatus());
    }

    @Test(expected = SessionGameServiceException.class)
    public void testIncreaseCardPriorityByUserNotInTurn() {
        session.setParticipantsCanAddCards(true);
        session.setCardCommentsAllowed(false);
        session = sessionService.updateSession(session);

        this.inviteTwoUsersAndLetThemJoin(session);
        sessionGameService.setUserJoined(session, organizer);
        Assert.assertTrue(session.isParticipantsCanAddCards());
        Assert.assertEquals(SessionStatus.ADDING_CARDS, session.getSessionStatus());

        sessionGameService.confirmAddedCards(session);
        Assert.assertEquals(SessionStatus.CHOOSING_CARDS, session.getSessionStatus());

        Set<CardDetails> cardDetailsSet = new HashSet<>();
        cardDetailsSet.add(cardDetails1);

        sessionGameService.chooseCards(session, player1, cardDetailsSet);

        cardDetailsSet = new HashSet<>();
        cardDetailsSet.add(cardDetails1);
        cardDetailsSet.add(cardDetails2);

        sessionGameService.chooseCards(session, player2, cardDetailsSet);

        cardDetailsSet = new HashSet<>();
        cardDetailsSet.add(cardDetails3);

        sessionGameService.chooseCards(session, organizer, cardDetailsSet);


        /*sessionGameService.confirmCardsChosen(session, player1);
        sessionGameService.confirmCardsChosen(session, player2);
        sessionGameService.confirmCardsChosen(session, organizer);*/

        Assert.assertEquals(SessionStatus.READY_TO_START, session.getSessionStatus());
        List<CardPosition> cardPositionList = session.getCardPositions();
        Comparator comparator = new Comparator<CardPosition>() {
            @Override
            public int compare(CardPosition o1, CardPosition o2) {
                return Integer.compare(o1.getCardPositionId(), o2.getCardPositionId());
            }
        };
        Collections.sort(cardPositionList, comparator);

        TreeSet set = new TreeSet(comparator);
        set.addAll(cardPositionList);

        Assert.assertArrayEquals(cardPositionList.toArray(), set.toArray());

        sessionGameService.startGame(session);
        Assert.assertEquals(SessionStatus.IN_PROGRESS, session.getSessionStatus());

        sessionGameService.increaseCardPriority(session, player2, cardDetails1);
    }

    @Test(expected = SessionGameServiceException.class)
    public void testIncreaseCardPriorityByUserThatIsntInSession() {
        session.setParticipantsCanAddCards(true);
        session.setCardCommentsAllowed(false);
        session = sessionService.updateSession(session);

        this.inviteTwoUsersAndLetThemJoin(session);
        sessionGameService.setUserJoined(session, organizer);
        Assert.assertTrue(session.isParticipantsCanAddCards());
        Assert.assertEquals(SessionStatus.ADDING_CARDS, session.getSessionStatus());

        sessionGameService.confirmAddedCards(session);
        Assert.assertEquals(SessionStatus.CHOOSING_CARDS, session.getSessionStatus());

        Set<CardDetails> cardDetailsSet = new HashSet<>();
        cardDetailsSet.add(cardDetails1);

        sessionGameService.chooseCards(session, player1, cardDetailsSet);

        cardDetailsSet = new HashSet<>();
        cardDetailsSet.add(cardDetails1);
        cardDetailsSet.add(cardDetails2);

        sessionGameService.chooseCards(session, player2, cardDetailsSet);

        cardDetailsSet = new HashSet<>();
        cardDetailsSet.add(cardDetails3);

        sessionGameService.chooseCards(session, organizer, cardDetailsSet);

        Assert.assertEquals(SessionStatus.READY_TO_START, session.getSessionStatus());
        List<CardPosition> cardPositionList = session.getCardPositions();
        Comparator comparator = new Comparator<CardPosition>() {
            @Override
            public int compare(CardPosition o1, CardPosition o2) {
                return Integer.compare(o1.getCardPositionId(), o2.getCardPositionId());
            }
        };
        Collections.sort(cardPositionList, comparator);

        TreeSet set = new TreeSet(comparator);
        set.addAll(cardPositionList);

        Assert.assertArrayEquals(cardPositionList.toArray(), set.toArray());

        sessionGameService.startGame(session);
        Assert.assertEquals(SessionStatus.IN_PROGRESS, session.getSessionStatus());

        sessionGameService.increaseCardPriority(session, player3, cardDetails1);
    }

    private void inviteTwoUsersAndLetThemJoin(Session session) {
        sessionGameService.inviteUserForSession(session, player1);
        sessionGameService.inviteUserForSession(session, player2);

        sessionGameService.confirmInvitedUsers(session);

        sessionGameService.setUserJoined(session, player1);
        sessionGameService.setUserJoined(session, player2);
    }
}
