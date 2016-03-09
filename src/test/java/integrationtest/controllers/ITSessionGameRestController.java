package integrationtest.controllers;

import be.kdg.kandoe.backend.model.cards.CardDetails;
import be.kdg.kandoe.backend.model.cards.CardPosition;
import be.kdg.kandoe.backend.model.oauth.OAuthClientDetails;
import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.backend.model.sessions.SessionStatus;
import be.kdg.kandoe.backend.model.sessions.SynchronousSession;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.*;
import be.kdg.kandoe.frontend.config.RootContextConfig;
import be.kdg.kandoe.frontend.config.WebContextConfig;
import be.kdg.kandoe.frontend.controller.resources.cards.CreateCardDetailsResource;
import integrationtest.IntegrationTestHelpers;
import integrationtest.TokenProvider;
import org.hamcrest.core.Is;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootContextConfig.class, WebContextConfig.class})
@WebAppConfiguration
@Transactional
public class ITSessionGameRestController {
    @Autowired
    private OAuthClientDetailsService oAuthClientDetailsService;

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

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    private String sessionOwnerUsername = "test-user-1";
    private String sessionOwnerPassword = "test-user-1";

    private User sessionOwner;
    private Organization organization;
    private Category category;
    
    private CardDetails cardDetails1;
    private CardDetails cardDetails2;
    private CardDetails cardDetails3;
    private CardDetails cardDetails4;
    private CardDetails cardDetails5;

    private OAuthClientDetails clientDetails;
    private String authorizationHeader;
    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        this.sessionOwner = new User(sessionOwnerUsername, sessionOwnerPassword);
        this.sessionOwner = userService.addUser(sessionOwner);

        OAuthClientDetails clientDetails = IntegrationTestHelpers.getOAuthClientDetails();
        this.clientDetails = oAuthClientDetailsService.addClientsDetails(clientDetails);

        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .addFilter(springSecurityFilterChain)
                .build();

        String token = TokenProvider.getToken(mockMvc, clientDetails, sessionOwnerUsername, sessionOwnerPassword);
        authorizationHeader = String.format("Bearer %s", token);

        createOrganizationAndCategory();
        addCardDetailsToCategory();
    }

    private void createOrganizationAndCategory() {
        organization = new Organization("test-organization-1", sessionOwner);
        organization = organizationService.addOrganization(organization);

        category = new Category();
        category.setName("test-category-1");
        category.setDescription("Description of test category 1");
        category.setOrganization(organization);
        category = categoryService.addCategory(category);
    }

    private void addCardDetailsToCategory() {
        cardDetails1 = new CardDetails();
        cardDetails1.setCategory(category);
        cardDetails1.setCreator(sessionOwner);
        cardDetails1.setText("Card 1");

        cardDetails2 = new CardDetails();
        cardDetails2.setCategory(category);
        cardDetails2.setCreator(sessionOwner);
        cardDetails2.setText("Card 2");

        cardDetails3 = new CardDetails();
        cardDetails3.setCategory(category);
        cardDetails3.setCreator(sessionOwner);
        cardDetails3.setText("Card 3");

        cardDetails4 = new CardDetails();
        cardDetails4.setCategory(category);
        cardDetails4.setCreator(sessionOwner);
        cardDetails4.setText("Card 4");

        cardDetails5 = new CardDetails();
        cardDetails5.setCategory(category);
        cardDetails5.setCreator(sessionOwner);
        cardDetails5.setText("Card 5");

        cardDetails1 = cardService.addCardDetailsToCategory(category, cardDetails1);
        cardDetails2 = cardService.addCardDetailsToCategory(category, cardDetails2);
        cardDetails3 = cardService.addCardDetailsToCategory(category, cardDetails3);
        cardDetails4 = cardService.addCardDetailsToCategory(category, cardDetails4);
        cardDetails5 = cardService.addCardDetailsToCategory(category, cardDetails5);
    }

    @Test
    public void getCardPositionsOfSession() throws Exception {
        String participant1Username = "participant-1";
        String participant1Password = "participant-1-pass";
        
        User participant1 = new User(participant1Username, participant1Password);
        participant1 = userService.addUser(participant1);
        
        Session session = new SynchronousSession();
        session.setOrganizer(sessionOwner);
        session.setCategory(category);
        session.setAmountOfCircles(5);
        session.setParticipantsCanAddCards(false);
        session.setCardCommentsAllowed(false);
        
        session.getCardPositions().add(new CardPosition(cardDetails1, session));
        session.getCardPositions().add(new CardPosition(cardDetails2, session));
        session.getCardPositions().add(new CardPosition(cardDetails3, session));
        session.getCardPositions().add(new CardPosition(cardDetails4, session));
        session.getCardPositions().add(new CardPosition(cardDetails5, session));
        
        session = sessionService.addSession(session);
        
        sessionGameService.inviteUserForSession(session, participant1);
        sessionGameService.setUserJoined(session, sessionOwner);
        sessionGameService.setUserJoined(session, participant1);

        Assert.assertEquals(SessionStatus.CHOOSING_CARDS, session.getSessionStatus());
        
        String stringResponse = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/sessions/" + session.getSessionId() + "/positions")
                .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andReturn().getResponse().getContentAsString();

        //JSONObject jsonResponse = new JSONObject(stringResponse);
        JSONArray jsonArray = new JSONArray(stringResponse);
        
        Assert.assertEquals(5, jsonArray.length());
    }
    
    @Test
    public void getCardPositionsOfSessionAsNonParticipantResultsInForbidden() throws Exception {
        String nonParticipantUsername = "non-participant-1";
        String nonParticipantPassword = "non-participant-1";
        
        User nonParticipant = new User(nonParticipantUsername, nonParticipantPassword);
        nonParticipant = userService.addUser(nonParticipant);

        Session session = new SynchronousSession();
        session.setOrganizer(sessionOwner);
        session.setCategory(category);
        session.setAmountOfCircles(5);
        session.setParticipantsCanAddCards(false);
        session.setCardCommentsAllowed(false);

        session.getCardPositions().add(new CardPosition(cardDetails1, session));
        session.getCardPositions().add(new CardPosition(cardDetails2, session));
        session.getCardPositions().add(new CardPosition(cardDetails3, session));
        session.getCardPositions().add(new CardPosition(cardDetails4, session));
        session.getCardPositions().add(new CardPosition(cardDetails5, session));

        session = sessionService.addSession(session);

        String token = TokenProvider.getToken(mockMvc, clientDetails, nonParticipantUsername, nonParticipantPassword);
        authorizationHeader = String.format("Bearer %s", token);
        
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/sessions/" + session.getSessionId() + "/positions")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isForbidden());
    }
    
    private Session createDefaultSession() {
        Session session = new SynchronousSession();
        session.setOrganizer(sessionOwner);
        session.setCategory(category);
        session.setAmountOfCircles(5);
        session.setParticipantsCanAddCards(false);
        session.setCardCommentsAllowed(false);

        /*session.getCardPositions().add(new CardPosition(cardDetails1, session));
        session.getCardPositions().add(new CardPosition(cardDetails2, session));
        session.getCardPositions().add(new CardPosition(cardDetails3, session));
        session.getCardPositions().add(new CardPosition(cardDetails4, session));
        session.getCardPositions().add(new CardPosition(cardDetails5, session));*/
        
        return sessionService.addSession(session);
    }
    
    @Test
    public void addCardDetailsToSessionWhenAddingCardsResultsInCreated() throws Exception {
        String participant1Username = "participant-1";
        String participant1Password = "participant-1-pass";
        
        User participant1 = new User(participant1Username, participant1Password);
        participant1 = userService.addUser(participant1);
        
        Session session = createDefaultSession();
        session.setParticipantsCanAddCards(true);
        session.setMinNumberOfCardsPerParticipant(1);
        session.setMaxNumberOfCardsPerParticipant(2);
        session = sessionService.updateSession(session);

        String tokenParticipant1 = TokenProvider.getToken(mockMvc, clientDetails, participant1Username, participant1Password);
        String authorizationHeaderParticipant1 = String.format("Bearer %s", tokenParticipant1);
        
        /*sessionGameService.inviteUserForSession(session, participant1);
        sessionGameService.setUserJoined(session, sessionOwner);
        sessionGameService.setUserJoined(session, participant1);*/
        
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite")
                        .header("Authorization", authorizationHeader)
                        .param("userId", String.valueOf(participant1.getUserId()))
        ).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isCreated());
        
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/join")
                    .header("Authorization", authorizationHeaderParticipant1)
        ).andExpect(MockMvcResultMatchers.status().isCreated());
        
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/join")
                    .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());
        
        String stringAllCardsResponse = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/sessions/" + session.getSessionId() + "/all-cards")
                    .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andReturn().getResponse().getContentAsString();
        
        JSONArray jsonAllCardsArray = new JSONArray(stringAllCardsResponse);
        Assert.assertEquals(5, jsonAllCardsArray.length());

        CreateCardDetailsResource createCardDetailsResource = new CreateCardDetailsResource();
        createCardDetailsResource.setText("Added card 1");
        
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/all-cards")
                    .header("Authorization", authorizationHeaderParticipant1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new JSONObject(createCardDetailsResource).toString())
        ).andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String stringAllCardsResponseAfterAdding = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/sessions/" + session.getSessionId() + "/all-cards")
                    .header("Authorization", authorizationHeaderParticipant1)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andReturn().getResponse().getContentAsString();
        
        JSONArray jsonAllCardsArrayAfterAdding = new JSONArray(stringAllCardsResponseAfterAdding);
        Assert.assertEquals(6, jsonAllCardsArrayAfterAdding.length());
    }
    
    @Test
    public void inviteUserAsNonOwnerResultsInForbidden() throws Exception {
        String participantUsername = "participant-1";
        String participantPassword = "participant-pass";
        
        String userToInviteUsername = "invited-user-1";
        String userToInvitePassword = "invited-user-pass";
        
        User participant = new User(participantUsername, participantPassword);
        User userToInvite = new User(userToInviteUsername, userToInvitePassword);
        
        participant = userService.addUser(participant);
        userToInvite = userService.addUser(userToInvite);
        
        Session session = createDefaultSession();

        String tokenParticipant = TokenProvider.getToken(mockMvc, clientDetails, participantUsername, participantPassword);
        String authorizationHeaderParticipant = String.format("Bearer %s", tokenParticipant);
        
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite")
                    .header("Authorization", authorizationHeaderParticipant)
                    .param("userId", String.valueOf(userToInvite.getUserId()))
        ).andExpect(MockMvcResultMatchers.status().isForbidden());
        
        session = sessionService.getSessionById(session.getSessionId());
        Assert.assertEquals(1, session.getParticipantInfo().size());
    }
    
    /*@Test
    public void allUsersChooseCardsResultsInStartedGame() throws Exception {
        Session session = createDefaultSession();

        String participant1Username = "participant-1";
        String participant1Password = "participant-1-pass";

        User participant1 = new User(participant1Username, participant1Password);
        participant1 = userService.addUser(participant1);

        String tokenParticipant1 = TokenProvider.getToken(mockMvc, clientDetails, participant1Username, participant1Password);
        String authorizationHeaderParticipant1 = String.format("Bearer %s", tokenParticipant1);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite")
                        .header("Authorization", authorizationHeader)
                        .param("userId", String.valueOf(participant1.getUserId()))
        ).andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/join")
                        .header("Authorization", authorizationHeaderParticipant1)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/join")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());
        
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/chosen-cards")
                    .param("cardDetailsId", String.valueOf(cardDetails1.getCardDetailsId()))
                    .param("cardDetailsId", String.valueOf(cardDetails3.getCardDetailsId()))
                    .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());
        
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/chosen-cards")
                    .param("cardDetailsId", String.valueOf(cardDetails3.getCardDetailsId()))
                    .param("cardDetailsId", String.valueOf(cardDetails4.getCardDetailsId()))
                    .header("Authorization", authorizationHeaderParticipant1)
        ).andExpect(MockMvcResultMatchers.status().isCreated());
        
        session = sessionService.getSessionById(session.getSessionId());
        
        Assert.assertEquals(SessionStatus.IN_PROGRESS, session.getSessionStatus());
        
        String stringChosenCards = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/sessions/" + session.getSessionId() + "/chosen-cards")
                    .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andReturn().getResponse().getContentAsString();
        
        JSONArray jsonArrayChosenCards = new JSONArray(stringChosenCards);
        Assert.assertEquals(3, jsonArrayChosenCards.length());
    }*/
    
    /*@Test
    public void addCardDetailsToSessionWhenInProgressResultsInBadRequest() throws Exception {
        Session session = createDefaultSession();

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/join")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());
        
        Assert.fail();
    }
    
    @Test
    public void addCardDetailsToSessionWhenReviewingResultsInBadRequest() {
        Assert.fail();
    }
    
    @Test
    public void addCardDetailsToSessionAsNonParticipantResultsInForbidden() {
        Assert.fail();
    }
    
    @Test
    public void chooseCardsForSessionAsParticipantWhenAddingCardsResultsInCreated() {
        Assert.fail();
    }
    
    @Test
    public void chooseCardsForSessionAsParticipantWhenInProgressResultsInBadRequest() {
        Assert.fail();
    }
    
    @Test
    public void chooseCardsForSessionAsNonParticipantResultsInForbidden() {
        Assert.fail();
    }
    
    @Test
    public void reviewCardsAsParticipantWhenReviewingResultsInCreated() {
        Assert.fail();
    }
    
    @Test
    public void reviewCardsAsParticipantWhenInProgressResultsInBadRequest() {
        Assert.fail();
    }
    
    @Test
    public void reviewCardsAsNonParticipantResultsInForbidden() {
        Assert.fail();
    }
    
    @Test
    public void joinSessionWithoutInvitationResultsInForbidden() {
        Assert.fail();
    }*/
    
    // TODO : SERVICE TEST : Test user cannot add same card multiple times
    
    // TODO : Test if card positions are initialized when the session is started
    
    // TODO : Test you can only get card positions of started session (state READY_TO_START and later)
    
    // TODO : Test if card details of session isn't the same as card details of category
    
    // TODO : Test choose cards as participant
    
    // TODO : Test choose cards as non-participant results in 403 Forbidden
    
    // TODO : Test invalid session ID or user ID results in 400 Bad Request
}
