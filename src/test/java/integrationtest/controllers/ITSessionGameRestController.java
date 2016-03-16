package integrationtest.controllers;

import be.kdg.kandoe.backend.model.cards.CardDetails;
import be.kdg.kandoe.backend.model.cards.CardPosition;
import be.kdg.kandoe.backend.model.oauth.OAuthClientDetails;
import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.organizations.Topic;
import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.backend.model.sessions.SessionStatus;
import be.kdg.kandoe.backend.model.sessions.SynchronousSession;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.*;
import be.kdg.kandoe.frontend.config.RootContextConfig;
import be.kdg.kandoe.frontend.config.WebContextConfig;
import be.kdg.kandoe.frontend.controller.resources.cards.CreateCardDetailsResource;
import be.kdg.kandoe.frontend.controller.resources.sessions.reviews.CreateCardReviewOverview;
import integrationtest.IntegrationTestHelpers;
import integrationtest.TokenProvider;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    private TopicService topicService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    private String sessionOwnerUsername = "test-user-1";
    private String sessionOwnerPassword = "test-user-1";

    private User sessionOwner;
    private Organization organization;
    private Category category;
    private Topic topic;

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

        topic = new Topic();
        topic.setName("test-topic-1");
        topic.setDescription("test-topic-1");
        topic.setCategory(this.category);
        topic = topicService.addTopic(topic);
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

        cardService.addCardDetailsToTopic(topic, cardDetails1);
        cardService.addCardDetailsToTopic(topic, cardDetails2);
        cardService.addCardDetailsToTopic(topic, cardDetails3);
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
        sessionGameService.confirmInvitedUsers(session);
        sessionGameService.setUserJoined(session, sessionOwner);
        sessionGameService.setUserJoined(session, participant1);

        Assert.assertEquals(SessionStatus.CHOOSING_CARDS, session.getSessionStatus());
        
        String stringResponse = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/sessions/" + session.getSessionId() + "/positions")
                .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$").isArray())
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
        participant1.setEmail("test@mail.com");
        participant1 = userService.addUser(participant1);
        
        Session session = createDefaultSession();
        session.setParticipantsCanAddCards(true);
        session.setMinNumberOfCardsPerParticipant(1);
        session.setMaxNumberOfCardsPerParticipant(2);
        session = sessionService.updateSession(session);

        String tokenParticipant1 = TokenProvider.getToken(mockMvc, clientDetails, participant1Username, participant1Password);
        String authorizationHeaderParticipant1 = String.format("Bearer %s", tokenParticipant1);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite")
                        .header("Authorization", authorizationHeader)
                        .param("email", String.valueOf(participant1.getEmail()))
        )/*.andDo(MockMvcResultHandlers.print())*/.andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite/confirm")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());
        
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
                .andExpect(jsonPath("$").isArray())
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
                .andExpect(jsonPath("$").isArray())
                .andReturn().getResponse().getContentAsString();
        
        JSONArray jsonAllCardsArrayAfterAdding = new JSONArray(stringAllCardsResponseAfterAdding);
        Assert.assertEquals(6, jsonAllCardsArrayAfterAdding.length());
    }
    
    @Test
    public void inviteUserAsNonOrganizerResultsInForbidden() throws Exception {
        String participantUsername = "participant-1";
        String participantPassword = "participant-pass";
        
        String userToInviteUsername = "invited-user-1";
        String userToInvitePassword = "invited-user-pass";
        
        User participant = new User(participantUsername, participantPassword);
        User userToInvite = new User(userToInviteUsername, userToInvitePassword);
        
        participant = userService.addUser(participant);
        participant.setEmail("test3@mail.com");
        userToInvite.setEmail("test@mail.com");
        userToInvite = userService.addUser(userToInvite);
        userToInvite.setEmail("test2@mail.com");
        
        Session session = createDefaultSession();
        
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite")
                    .header("Authorization", authorizationHeader)
                        .param("email",participant.getEmail())
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite/confirm")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        String tokenParticipant = TokenProvider.getToken(mockMvc, clientDetails, participantUsername, participantPassword);
        String authorizationHeaderParticipant = String.format("Bearer %s", tokenParticipant);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/join")
                        .header("Authorization", authorizationHeaderParticipant)
        ).andExpect(MockMvcResultMatchers.status().isCreated());
        
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite")
                    .header("Authorization", authorizationHeaderParticipant)
                    .param("email", userToInvite.getEmail())
        ).andExpect(MockMvcResultMatchers.status().isForbidden());
        
        session = sessionService.getSessionById(session.getSessionId());
        Assert.assertEquals(2, session.getParticipantInfo().size());
    }
    
    @Test
    public void allParticipantsChooseCardsAndStartGameResultsInStartedGame() throws Exception {
        Session session = createDefaultSession();
        session.setMinNumberOfCardsPerParticipant(2);
        session = sessionService.updateSession(session);

        String participant1Username = "participant-1";
        String participant1Password = "participant-1-pass";

        User participant1 = new User(participant1Username, participant1Password);
        participant1.setEmail("test@mail.com");
        participant1 = userService.addUser(participant1);

        String tokenParticipant1 = TokenProvider.getToken(mockMvc, clientDetails, participant1Username, participant1Password);
        String authorizationHeaderParticipant1 = String.format("Bearer %s", tokenParticipant1);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite")
                        .header("Authorization", authorizationHeader)
                        .param("email", participant1.getEmail())
        ).andDo(print()).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite/confirm")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

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

        session = sessionService.getSessionById(session.getSessionId());
        Assert.assertEquals(SessionStatus.CHOOSING_CARDS, session.getSessionStatus());
        
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/chosen-cards")
                    .param("cardDetailsId", String.valueOf(cardDetails3.getCardDetailsId()))
                    .param("cardDetailsId", String.valueOf(cardDetails4.getCardDetailsId()))
                    .header("Authorization", authorizationHeaderParticipant1)
        ).andExpect(MockMvcResultMatchers.status().isCreated());
        
        session = sessionService.getSessionById(session.getSessionId());
        Assert.assertEquals(SessionStatus.READY_TO_START, session.getSessionStatus());
        
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/start")
                    .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        session = sessionService.getSessionById(session.getSessionId());
        Assert.assertEquals(SessionStatus.IN_PROGRESS, session.getSessionStatus());
        
        String stringChosenCards = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/sessions/" + session.getSessionId() + "/positions")
                    .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andReturn().getResponse().getContentAsString();
        
        JSONArray jsonArrayChosenCards = new JSONArray(stringChosenCards);
        Assert.assertEquals(3, jsonArrayChosenCards.length());
    }
    
    @Test
    public void addCardDetailsToSessionWhenInProgressResultsInBadRequest() throws Exception {
        Session session = createDefaultSession();
        session.setMinNumberOfCardsPerParticipant(1);
        session.setParticipantsCanAddCards(true);
        session = sessionService.updateSession(session);

        String participant1Username = "participant-1";
        String participant1Password = "participant-1-pass";

        User participant1 = new User(participant1Username, participant1Password);
        participant1.setEmail("test@mail.com");
        participant1 = userService.addUser(participant1);

        String tokenParticipant1 = TokenProvider.getToken(mockMvc, clientDetails, participant1Username, participant1Password);
        String authorizationHeaderParticipant1 = String.format("Bearer %s", tokenParticipant1);
        
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite")
                    .header("Authorization", authorizationHeader)
                        .param("email", participant1.getEmail())
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite/confirm")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());
        
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/join")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/join")
                        .header("Authorization", authorizationHeaderParticipant1)
        ).andExpect(MockMvcResultMatchers.status().isCreated());
        
        CreateCardDetailsResource createCardDetailsResource = new CreateCardDetailsResource();
        createCardDetailsResource.setText("Added card #1");
        
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/all-cards")
                        .header("Authorization", authorizationHeaderParticipant1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new JSONObject(createCardDetailsResource).toString())
        ).andExpect(MockMvcResultMatchers.status().isCreated());
        
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/all-cards/confirm")
                    .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/all-cards/confirm")
                        .header("Authorization", authorizationHeaderParticipant1)
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
                        .header("Authorization", authorizationHeaderParticipant1)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/start")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        createCardDetailsResource = new CreateCardDetailsResource();
        createCardDetailsResource.setText("Added card #2");
        
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/all-cards")
                        .header("Authorization", authorizationHeaderParticipant1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new JSONObject(createCardDetailsResource).toString())
        ).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
    
    @Test
    public void chooseCardWhenAlreadyConfirmed() throws Exception {

        Session session = createDefaultSession();
        session.setMinNumberOfCardsPerParticipant(1);
        session.setParticipantsCanAddCards(true);
        session = sessionService.updateSession(session);

        String participant1Username = "participant-1";
        String participant1Password = "participant-1-pass";

        User participant1 = new User(participant1Username, participant1Password);
        participant1.setEmail("test@mail.com");
        participant1 = userService.addUser(participant1);

        String tokenParticipant1 = TokenProvider.getToken(mockMvc, clientDetails, participant1Username, participant1Password);
        String authorizationHeaderParticipant1 = String.format("Bearer %s", tokenParticipant1);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite")
                        .header("Authorization", authorizationHeader)
                        .param("email", participant1.getEmail())
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite/confirm")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/join")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/join")
                        .header("Authorization", authorizationHeaderParticipant1)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        CreateCardDetailsResource createCardDetailsResource = new CreateCardDetailsResource();
        createCardDetailsResource.setText("Added card #1");

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/all-cards")
                        .header("Authorization", authorizationHeaderParticipant1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new JSONObject(createCardDetailsResource).toString())
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/all-cards/confirm")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/all-cards/confirm")
                        .header("Authorization", authorizationHeaderParticipant1)
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
                        .header("Authorization", authorizationHeaderParticipant1)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/start")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/chosen-cards")
                        .param("cardDetailsId", String.valueOf(cardDetails3.getCardDetailsId()))
                        .header("Authorization", authorizationHeaderParticipant1)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest());

    }
    
    @Test
    public void startGameWithoutOtherParticipantsResultsInBadRequest() throws Exception {
        Session session = createDefaultSession();
        session.setMinNumberOfCardsPerParticipant(1);
        session.setParticipantsCanAddCards(false);
        session.setCardCommentsAllowed(false);
        session = sessionService.updateSession(session);

        String participant1Username = "participant-1";
        String participant1Password = "participant-1-pass";

        User participant1 = new User(participant1Username, participant1Password);
        participant1.setEmail("test@mail.com");
        participant1 = userService.addUser(participant1);

        String tokenParticipant1 = TokenProvider.getToken(mockMvc, clientDetails, participant1Username, participant1Password);
        String authorizationHeaderParticipant1 = String.format("Bearer %s", tokenParticipant1);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite")
                        .header("Authorization", authorizationHeader)
                        .param("email", participant1.getEmail())
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite/confirm")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/join")
                        .header("Authorization", authorizationHeaderParticipant1)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/start")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/chosen-cards")
                        .param("cardDetailsId", String.valueOf(cardDetails1.getCardDetailsId()))
                        .param("cardDetailsId", String.valueOf(cardDetails3.getCardDetailsId()))
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest());

    }


    /*
    @Test
    public void addCardDetailsToSessionWhenReviewingResultsInBadRequest() {
        Assert.fail();
    }
    */

    @Test
    public void addCardDetailsToSessionAsNonParticipantResultsInForbidden() throws Exception {
        String participant1Username = "participant-1";
        String participant1Password = "participant-1-pass";

        User participant1 = new User(participant1Username, participant1Password);
        participant1.setEmail("test@mail.com");
        participant1 = userService.addUser(participant1);

        Session session = createDefaultSession();
        session.setParticipantsCanAddCards(true);
        session.setMinNumberOfCardsPerParticipant(1);
        session.setMaxNumberOfCardsPerParticipant(2);
        session = sessionService.updateSession(session);

        String tokenParticipant1 = TokenProvider.getToken(mockMvc, clientDetails, participant1Username, participant1Password);
        String authorizationHeaderParticipant1 = String.format("Bearer %s", tokenParticipant1);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite")
                        .header("Authorization", authorizationHeader)
                        .param("email", participant1.getEmail())
        )/*.andDo(MockMvcResultHandlers.print())*/.andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite/confirm")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());
        
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
                .andExpect(jsonPath("$").isArray())
                .andReturn().getResponse().getContentAsString();

        JSONArray jsonAllCardsArray = new JSONArray(stringAllCardsResponse);
        Assert.assertEquals(5, jsonAllCardsArray.length());

        CreateCardDetailsResource createCardDetailsResource = new CreateCardDetailsResource();
        createCardDetailsResource.setText("Added card 1");

        User nonUser = new User("not-joined-user", "pass");

        userService.addUser(nonUser);

        String tokenNonUser = TokenProvider.getToken(mockMvc, clientDetails, nonUser.getUsername(), "pass");
        String authorizationHeaderNonUser = String.format("Bearer %s", tokenNonUser);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/all-cards")
                        .header("Authorization", authorizationHeaderNonUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new JSONObject(createCardDetailsResource).toString())
        ).andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void chooseCardsForSessionAsNonParticipantResultsInForbidden() throws Exception {
        Session session = createDefaultSession();
        session.setMinNumberOfCardsPerParticipant(2);
        session = sessionService.updateSession(session);

        String participant1Username = "participant-1";
        String participant1Password = "participant-1-pass";

        User participant1 = new User(participant1Username, participant1Password);
        participant1.setEmail("test@mail.com");
        participant1 = userService.addUser(participant1);

        String tokenParticipant1 = TokenProvider.getToken(mockMvc, clientDetails, participant1Username, participant1Password);
        String authorizationHeaderParticipant1 = String.format("Bearer %s", tokenParticipant1);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite")
                        .header("Authorization", authorizationHeader)
                        .param("email", participant1.getEmail())
        ).andDo(print()).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite/confirm")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

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

        session = sessionService.getSessionById(session.getSessionId());
        Assert.assertEquals(SessionStatus.CHOOSING_CARDS, session.getSessionStatus());

        User nonUser = new User("not-joined-user", "pass");

        userService.addUser(nonUser);

        String tokenNonUser = TokenProvider.getToken(mockMvc, clientDetails, nonUser.getUsername(), "pass");
        String authorizationHeaderNonUser = String.format("Bearer %s", tokenNonUser);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/chosen-cards")
                        .param("cardDetailsId", String.valueOf(cardDetails3.getCardDetailsId()))
                        .param("cardDetailsId", String.valueOf(cardDetails4.getCardDetailsId()))
                        .header("Authorization", authorizationHeaderNonUser)
        ).andExpect(MockMvcResultMatchers.status().isForbidden());

    }


    @Test
    public void joinSessionWithoutInvitationResultsInForbidden() throws Exception {

        Session session = createDefaultSession();
        session.setMinNumberOfCardsPerParticipant(1);
        session.setParticipantsCanAddCards(false);
        session.setCardCommentsAllowed(false);
        session = sessionService.updateSession(session);

        String participant1Username = "participant-1";
        String participant1Password = "participant-1-pass";

        User participant1 = new User(participant1Username, participant1Password);
        participant1.setEmail("test@mail.com");
        participant1 = userService.addUser(participant1);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite")
                        .header("Authorization", authorizationHeader)
                        .param("email", participant1.getEmail())
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        User nonUser = new User("not-joined-user", "pass");

        userService.addUser(nonUser);

        String tokenNonUser = TokenProvider.getToken(mockMvc, clientDetails, nonUser.getUsername(), "pass");
        String authorizationHeaderNonUser = String.format("Bearer %s", tokenNonUser);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/join")
                        .header("Authorization", authorizationHeaderNonUser)
        ).andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void addCardToTopicResultsIntoCreated() throws Exception {
        String participant1Username = "participant-1";
        String participant1Password = "participant-1-pass";

        User participant1 = new User(participant1Username, participant1Password);
        participant1.setEmail("test@mail.com");
        participant1 = userService.addUser(participant1);

        Session session = createDefaultSession();
        session.setParticipantsCanAddCards(true);
        session.setTopic(topic);
        session.setMinNumberOfCardsPerParticipant(1);
        session.setMaxNumberOfCardsPerParticipant(2);
        session = sessionService.updateSession(session);

        String tokenParticipant1 = TokenProvider.getToken(mockMvc, clientDetails, participant1Username, participant1Password);
        String authorizationHeaderParticipant1 = String.format("Bearer %s", tokenParticipant1);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite")
                        .header("Authorization", authorizationHeader)
                        .param("email", participant1.getEmail())
        )/*.andDo(MockMvcResultHandlers.print())*/.andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite/confirm")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());
        
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
                .andExpect(jsonPath("$").isArray())
                .andReturn().getResponse().getContentAsString();

        JSONArray jsonAllCardsArray = new JSONArray(stringAllCardsResponse);
        Assert.assertEquals(3, jsonAllCardsArray.length());

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
                .andExpect(jsonPath("$").isArray())
                .andReturn().getResponse().getContentAsString();

        JSONArray jsonAllCardsArrayAfterAdding = new JSONArray(stringAllCardsResponseAfterAdding);
        Assert.assertEquals(4, jsonAllCardsArrayAfterAdding.length());
    }

    @Test
    public void testStartingNonExisitingSession() throws Exception {
        String participant1Username = "participant-1";
        String participant1Password = "participant-1-pass";

        User participant1 = new User(participant1Username, participant1Password);
        participant1.setEmail("test@mail.com");
        participant1 = userService.addUser(participant1);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + -1 +  "/invite")
                        .header("Authorization", authorizationHeader)
                        .param("userId", String.valueOf(participant1.getUserId()))
        ).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testIncreaseCardPosition() throws Exception {
        Session session = createDefaultSession();
        session.setMinNumberOfCardsPerParticipant(2);
        session = sessionService.updateSession(session);

        String participant1Username = "participant-1";
        String participant1Password = "participant-1-pass";

        User participant1 = new User(participant1Username, participant1Password);
        participant1.setEmail("test@mail.com");
        participant1 = userService.addUser(participant1);

        String tokenParticipant1 = TokenProvider.getToken(mockMvc, clientDetails, participant1Username, participant1Password);
        String authorizationHeaderParticipant1 = String.format("Bearer %s", tokenParticipant1);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite")
                        .header("Authorization", authorizationHeader)
                        .param("email", participant1.getEmail())
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite/confirm")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

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

        session = sessionService.getSessionById(session.getSessionId());
        Assert.assertEquals(SessionStatus.CHOOSING_CARDS, session.getSessionStatus());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/chosen-cards")
                        .param("cardDetailsId", String.valueOf(cardDetails3.getCardDetailsId()))
                        .param("cardDetailsId", String.valueOf(cardDetails4.getCardDetailsId()))
                        .header("Authorization", authorizationHeaderParticipant1)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        session = sessionService.getSessionById(session.getSessionId());
        Assert.assertEquals(SessionStatus.READY_TO_START, session.getSessionStatus());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/start")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        session = sessionService.getSessionById(session.getSessionId());
        Assert.assertEquals(SessionStatus.IN_PROGRESS, session.getSessionStatus());

        String stringChosenCards = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/sessions/" + session.getSessionId() + "/positions")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andReturn().getResponse().getContentAsString();

        JSONArray jsonArrayChosenCards = new JSONArray(stringChosenCards);
        Assert.assertEquals(3, jsonArrayChosenCards.length());

        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/sessions/" + session.getSessionId() + "/positions")
                        .header("Authorization", authorizationHeaderParticipant1)
                        .param("cardDetailsId", String.valueOf(cardDetails1.getCardDetailsId()))
        ).andDo(print()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(jsonPath("$.priority", is(2)));
    }

    @Test
    public void testEndGame() throws Exception {
        Session session = createDefaultSession();
        session.setMinNumberOfCardsPerParticipant(2);
        session.setAmountOfCircles(1);
        session = sessionService.updateSession(session);

        String participant1Username = "participant-1";
        String participant1Password = "participant-1-pass";

        User participant1 = new User(participant1Username, participant1Password);
        participant1.setEmail("test@mail.com");
        participant1 = userService.addUser(participant1);

        String tokenParticipant1 = TokenProvider.getToken(mockMvc, clientDetails, participant1Username, participant1Password);
        String authorizationHeaderParticipant1 = String.format("Bearer %s", tokenParticipant1);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite")
                        .header("Authorization", authorizationHeader)
                        .param("email", participant1.getEmail())
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite/confirm")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

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

        session = sessionService.getSessionById(session.getSessionId());
        Assert.assertEquals(SessionStatus.CHOOSING_CARDS, session.getSessionStatus());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/chosen-cards")
                        .param("cardDetailsId", String.valueOf(cardDetails3.getCardDetailsId()))
                        .param("cardDetailsId", String.valueOf(cardDetails4.getCardDetailsId()))
                        .header("Authorization", authorizationHeaderParticipant1)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        session = sessionService.getSessionById(session.getSessionId());
        Assert.assertEquals(SessionStatus.READY_TO_START, session.getSessionStatus());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/start")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        session = sessionService.getSessionById(session.getSessionId());
        Assert.assertEquals(SessionStatus.IN_PROGRESS, session.getSessionStatus());

        String stringChosenCards = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/sessions/" + session.getSessionId() + "/positions")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andReturn().getResponse().getContentAsString();

        JSONArray jsonArrayChosenCards = new JSONArray(stringChosenCards);
        Assert.assertEquals(3, jsonArrayChosenCards.length());

        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/sessions/" + session.getSessionId() + "/positions")
                        .header("Authorization", authorizationHeaderParticipant1)
                        .param("cardDetailsId", String.valueOf(cardDetails1.getCardDetailsId()))
        ).andDo(print()).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.priority", is(2)));

        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/sessions/" + session.getSessionId() + "/positions")
                        .header("Authorization", authorizationHeader)
                        .param("cardDetailsId", String.valueOf(cardDetails1.getCardDetailsId()))
        ).andDo(print()).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.priority", is(3)));

        Assert.assertEquals(SessionStatus.FINISHED, session.getSessionStatus());
    }
    
    @Test
    public void testIncreaseCardPositionByNotTurnUser() throws Exception {
        Session session = createDefaultSession();
        session.setMinNumberOfCardsPerParticipant(2);
        session = sessionService.updateSession(session);

        String participant1Username = "participant-1";
        String participant1Password = "participant-1-pass";

        User participant1 = new User(participant1Username, participant1Password);
        participant1.setEmail("test@mail.com");
        participant1 = userService.addUser(participant1);

        String tokenParticipant1 = TokenProvider.getToken(mockMvc, clientDetails, participant1Username, participant1Password);
        String authorizationHeaderParticipant1 = String.format("Bearer %s", tokenParticipant1);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite")
                        .header("Authorization", authorizationHeader)
                        .param("email", participant1.getEmail())
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite/confirm")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

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

        session = sessionService.getSessionById(session.getSessionId());
        Assert.assertEquals(SessionStatus.CHOOSING_CARDS, session.getSessionStatus());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/chosen-cards")
                        .param("cardDetailsId", String.valueOf(cardDetails3.getCardDetailsId()))
                        .param("cardDetailsId", String.valueOf(cardDetails4.getCardDetailsId()))
                        .header("Authorization", authorizationHeaderParticipant1)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        session = sessionService.getSessionById(session.getSessionId());
        Assert.assertEquals(SessionStatus.READY_TO_START, session.getSessionStatus());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/start")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        session = sessionService.getSessionById(session.getSessionId());
        Assert.assertEquals(SessionStatus.IN_PROGRESS, session.getSessionStatus());

        String stringChosenCards = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/sessions/" + session.getSessionId() + "/positions")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andReturn().getResponse().getContentAsString();

        JSONArray jsonArrayChosenCards = new JSONArray(stringChosenCards);
        Assert.assertEquals(3, jsonArrayChosenCards.length());

        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/sessions/" + session.getSessionId() + "/positions")
                        .header("Authorization", authorizationHeader)
                        .param("cardDetailsId", String.valueOf(cardDetails1.getCardDetailsId()))
        ).andDo(print()).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testForceEndGame() throws Exception {
        Session session = createDefaultSession();
        session.setMinNumberOfCardsPerParticipant(2);
        session.setAmountOfCircles(1);
        session = sessionService.updateSession(session);

        String participant1Username = "participant-1";
        String participant1Password = "participant-1-pass";

        User participant1 = new User(participant1Username, participant1Password);
        participant1.setEmail("test@mail.com");
        participant1 = userService.addUser(participant1);

        String tokenParticipant1 = TokenProvider.getToken(mockMvc, clientDetails, participant1Username, participant1Password);
        String authorizationHeaderParticipant1 = String.format("Bearer %s", tokenParticipant1);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite")
                        .header("Authorization", authorizationHeader)
                        .param("email", participant1.getEmail())
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite/confirm")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

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

        session = sessionService.getSessionById(session.getSessionId());
        Assert.assertEquals(SessionStatus.CHOOSING_CARDS, session.getSessionStatus());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/chosen-cards")
                        .param("cardDetailsId", String.valueOf(cardDetails3.getCardDetailsId()))
                        .param("cardDetailsId", String.valueOf(cardDetails4.getCardDetailsId()))
                        .header("Authorization", authorizationHeaderParticipant1)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        session = sessionService.getSessionById(session.getSessionId());
        Assert.assertEquals(SessionStatus.READY_TO_START, session.getSessionStatus());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/start")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        session = sessionService.getSessionById(session.getSessionId());
        Assert.assertEquals(SessionStatus.IN_PROGRESS, session.getSessionStatus());

        String stringChosenCards = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/sessions/" + session.getSessionId() + "/positions")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andReturn().getResponse().getContentAsString();

        JSONArray jsonArrayChosenCards = new JSONArray(stringChosenCards);
        Assert.assertEquals(3, jsonArrayChosenCards.length());

        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/sessions/" + session.getSessionId() + "/positions")
                        .header("Authorization", authorizationHeaderParticipant1)
                        .param("cardDetailsId", String.valueOf(cardDetails1.getCardDetailsId()))
        ).andDo(print()).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.priority", is(2)));

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/end")
                        .header("Authorization", authorizationHeader)
        ).andDo(print()).andExpect(MockMvcResultMatchers.status().isOk());

        Assert.assertEquals(SessionStatus.FINISHED, session.getSessionStatus());
    }

    @Test
    public void testForceEndGameByNonOrganizerResultsInForbidden() throws Exception {
        Session session = createDefaultSession();
        session.setMinNumberOfCardsPerParticipant(2);
        session.setAmountOfCircles(1);
        session = sessionService.updateSession(session);

        String participant1Username = "participant-1";
        String participant1Password = "participant-1-pass";

        User participant1 = new User(participant1Username, participant1Password);
        participant1.setEmail("test@mail.com");
        participant1 = userService.addUser(participant1);

        String tokenParticipant1 = TokenProvider.getToken(mockMvc, clientDetails, participant1Username, participant1Password);
        String authorizationHeaderParticipant1 = String.format("Bearer %s", tokenParticipant1);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite")
                        .header("Authorization", authorizationHeader)
                        .param("email", participant1.getEmail())
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite/confirm")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

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

        session = sessionService.getSessionById(session.getSessionId());
        Assert.assertEquals(SessionStatus.CHOOSING_CARDS, session.getSessionStatus());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/chosen-cards")
                        .param("cardDetailsId", String.valueOf(cardDetails3.getCardDetailsId()))
                        .param("cardDetailsId", String.valueOf(cardDetails4.getCardDetailsId()))
                        .header("Authorization", authorizationHeaderParticipant1)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        session = sessionService.getSessionById(session.getSessionId());
        Assert.assertEquals(SessionStatus.READY_TO_START, session.getSessionStatus());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/start")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        session = sessionService.getSessionById(session.getSessionId());
        Assert.assertEquals(SessionStatus.IN_PROGRESS, session.getSessionStatus());

        String stringChosenCards = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/sessions/" + session.getSessionId() + "/positions")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andReturn().getResponse().getContentAsString();

        JSONArray jsonArrayChosenCards = new JSONArray(stringChosenCards);
        Assert.assertEquals(3, jsonArrayChosenCards.length());

        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/sessions/" + session.getSessionId() + "/positions")
                        .header("Authorization", authorizationHeaderParticipant1)
                        .param("cardDetailsId", String.valueOf(cardDetails1.getCardDetailsId()))
        ).andDo(print()).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.priority", is(2)));

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/end")
                        .header("Authorization", authorizationHeaderParticipant1)
        ).andDo(print()).andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testAddReviewToSessionInReviewingStage() throws Exception {
        String participant1Username = "participant-1";
        String participant1Password = "participant-1-pass";

        User participant1 = new User(participant1Username, participant1Password);
        participant1.setEmail("test@mail.com");
        participant1 = userService.addUser(participant1);

        Session session = createDefaultSession();
        session.setCardCommentsAllowed(true);
        session.setParticipantsCanAddCards(true);
        session.setMinNumberOfCardsPerParticipant(1);
        session.setMaxNumberOfCardsPerParticipant(2);
        session = sessionService.updateSession(session);

        String tokenParticipant1 = TokenProvider.getToken(mockMvc, clientDetails, participant1Username, participant1Password);
        String authorizationHeaderParticipant1 = String.format("Bearer %s", tokenParticipant1);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite")
                        .header("Authorization", authorizationHeader)
                        .param("email", participant1.getEmail())
        )/*.andDo(MockMvcResultHandlers.print())*/.andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite/confirm")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());
        
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/join")
                        .header("Authorization", authorizationHeaderParticipant1)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/join")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());


        CreateCardDetailsResource createCardDetailsResource = new CreateCardDetailsResource();
        createCardDetailsResource.setText("Added card 1");

        String cardAddedJson = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/all-cards")
                        .header("Authorization", authorizationHeaderParticipant1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new JSONObject(createCardDetailsResource).toString())
        ).andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/all-cards/confirm")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/all-cards/confirm")
                        .header("Authorization", authorizationHeaderParticipant1)
        ).andExpect(MockMvcResultMatchers.status().isCreated());


        Assert.assertEquals(SessionStatus.REVIEWING_CARDS, session.getSessionStatus());

        JSONObject cardAddedObject = new JSONObject(cardAddedJson);

        CreateCardReviewOverview cardReview = new CreateCardReviewOverview();
        cardReview.setCardDetailsId(cardAddedObject.getInt("cardDetailsId"));
        cardReview.setMessage("card-test-review");

        JSONObject reviewCard1 = new JSONObject(cardReview);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/reviews")
                        .header("Authorization", authorizationHeaderParticipant1)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(reviewCard1.toString())
        ).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(jsonPath("$.commentId").exists())
                .andExpect(jsonPath("$.commentId").isNotEmpty());
    }

    @Test
    public void testAddReviewToSessionInReviewingStageByUserNotInSession() throws Exception {
        String participant1Username = "participant-1";
        String participant1Password = "participant-1-pass";

        User participant1 = new User(participant1Username, participant1Password);
        participant1.setEmail("test@mail.com");
        participant1 = userService.addUser(participant1);

        Session session = createDefaultSession();
        session.setCardCommentsAllowed(true);
        session.setParticipantsCanAddCards(true);
        session.setMinNumberOfCardsPerParticipant(1);
        session.setMaxNumberOfCardsPerParticipant(2);
        session = sessionService.updateSession(session);

        String tokenParticipant1 = TokenProvider.getToken(mockMvc, clientDetails, participant1Username, participant1Password);
        String authorizationHeaderParticipant1 = String.format("Bearer %s", tokenParticipant1);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite")
                        .header("Authorization", authorizationHeader)
                        .param("email", participant1.getEmail())
        )/*.andDo(MockMvcResultHandlers.print())*/.andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite/confirm")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());
        
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/join")
                        .header("Authorization", authorizationHeaderParticipant1)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/join")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());


        CreateCardDetailsResource createCardDetailsResource = new CreateCardDetailsResource();
        createCardDetailsResource.setText("Added card 1");

        String cardAddedJson = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/all-cards")
                        .header("Authorization", authorizationHeaderParticipant1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new JSONObject(createCardDetailsResource).toString())
        ).andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/all-cards/confirm")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/all-cards/confirm")
                        .header("Authorization", authorizationHeaderParticipant1)
        ).andExpect(MockMvcResultMatchers.status().isCreated());


        Assert.assertEquals(SessionStatus.REVIEWING_CARDS, session.getSessionStatus());

        JSONObject cardAddedObject = new JSONObject(cardAddedJson);

        CreateCardReviewOverview cardReview = new CreateCardReviewOverview();
        cardReview.setCardDetailsId(cardAddedObject.getInt("cardDetailsId"));
        cardReview.setMessage("card-test-review");

        JSONObject reviewCard1 = new JSONObject(cardReview);

        User nonUser = new User("nonparticipant", "pass");
        userService.addUser(nonUser);

        String tokenNonParticipant = TokenProvider.getToken(mockMvc, clientDetails, nonUser.getUsername(), "pass");
        String authorizationHeaderNonParticipant = String.format("Bearer %s", tokenNonParticipant);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/reviews")
                        .header("Authorization", authorizationHeaderNonParticipant)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(reviewCard1.toString())
        ).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void testAddReviewToSessionThatDoesntAllowReviewingCards() throws Exception {
        String participant1Username = "participant-1";
        String participant1Password = "participant-1-pass";

        User participant1 = new User(participant1Username, participant1Password);
        participant1.setEmail("test@mail.com");
        participant1 = userService.addUser(participant1);

        Session session = createDefaultSession();
        session.setCardCommentsAllowed(false);
        session.setParticipantsCanAddCards(true);
        session.setMinNumberOfCardsPerParticipant(1);
        session.setMaxNumberOfCardsPerParticipant(2);
        session = sessionService.updateSession(session);

        String tokenParticipant1 = TokenProvider.getToken(mockMvc, clientDetails, participant1Username, participant1Password);
        String authorizationHeaderParticipant1 = String.format("Bearer %s", tokenParticipant1);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite")
                        .header("Authorization", authorizationHeader)
                        .param("email", participant1.getEmail())
        )/*.andDo(MockMvcResultHandlers.print())*/.andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite/confirm")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/join")
                        .header("Authorization", authorizationHeaderParticipant1)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/join")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());


        CreateCardDetailsResource createCardDetailsResource = new CreateCardDetailsResource();
        createCardDetailsResource.setText("Added card 1");

        String cardAddedJson = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/all-cards")
                        .header("Authorization", authorizationHeaderParticipant1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new JSONObject(createCardDetailsResource).toString())
        ).andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/all-cards/confirm")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/all-cards/confirm")
                        .header("Authorization", authorizationHeaderParticipant1)
        ).andExpect(MockMvcResultMatchers.status().isCreated());


        Assert.assertEquals(SessionStatus.CHOOSING_CARDS, session.getSessionStatus());

        JSONObject cardAddedObject = new JSONObject(cardAddedJson);

        CreateCardReviewOverview cardReview = new CreateCardReviewOverview();
        cardReview.setCardDetailsId(cardAddedObject.getInt("cardDetailsId"));
        cardReview.setMessage("card-test-review");

        JSONObject reviewCard1 = new JSONObject(cardReview);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/reviews")
                        .header("Authorization", authorizationHeaderParticipant1)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(reviewCard1.toString())
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void testConfirmReviewAndProgressToChoosingCards() throws Exception {
        String participant1Username = "participant-1";
        String participant1Password = "participant-1-pass";

        User participant1 = new User(participant1Username, participant1Password);
        participant1.setEmail("test@mail.com");
        participant1 = userService.addUser(participant1);

        Session session = createDefaultSession();
        session.setCardCommentsAllowed(true);
        session.setParticipantsCanAddCards(true);
        session.setMinNumberOfCardsPerParticipant(1);
        session.setMaxNumberOfCardsPerParticipant(2);
        session = sessionService.updateSession(session);

        String tokenParticipant1 = TokenProvider.getToken(mockMvc, clientDetails, participant1Username, participant1Password);
        String authorizationHeaderParticipant1 = String.format("Bearer %s", tokenParticipant1);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite")
                        .header("Authorization", authorizationHeader)
                        .param("email", participant1.getEmail())
        )/*.andDo(MockMvcResultHandlers.print())*/.andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite/confirm")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());
        
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/join")
                        .header("Authorization", authorizationHeaderParticipant1)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/join")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());


        CreateCardDetailsResource createCardDetailsResource = new CreateCardDetailsResource();
        createCardDetailsResource.setText("Added card 1");

        String cardAddedJson = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/all-cards")
                        .header("Authorization", authorizationHeaderParticipant1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new JSONObject(createCardDetailsResource).toString())
        ).andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/all-cards/confirm")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/all-cards/confirm")
                        .header("Authorization", authorizationHeaderParticipant1)
        ).andExpect(MockMvcResultMatchers.status().isCreated());


        Assert.assertEquals(SessionStatus.REVIEWING_CARDS, session.getSessionStatus());

        JSONObject cardAddedObject = new JSONObject(cardAddedJson);

        CreateCardReviewOverview cardReview1 = new CreateCardReviewOverview();
        cardReview1.setCardDetailsId(cardAddedObject.getInt("cardDetailsId"));
        cardReview1.setMessage("card-test-review");

        JSONObject reviewCard1 = new JSONObject(cardReview1);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/reviews")
                        .header("Authorization", authorizationHeaderParticipant1)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(reviewCard1.toString())
        )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(jsonPath("$.commentId").exists())
                .andExpect(jsonPath("$.commentId").isNotEmpty());

        CreateCardReviewOverview cardReview2 = new CreateCardReviewOverview();
        cardReview2.setCardDetailsId(cardAddedObject.getInt("cardDetailsId"));
        cardReview2.setMessage("card-test-review");

        JSONObject reviewCard2 = new JSONObject(cardReview2);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/reviews")
                        .header("Authorization", authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(reviewCard2.toString())
        )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(jsonPath("$.commentId").exists())
                .andExpect(jsonPath("$.commentId").isNotEmpty());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/reviews/confirm")
                        .header("Authorization", authorizationHeaderParticipant1)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/reviews/confirm")
                        .header("Authorization", authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
        ).andExpect(status().isCreated());
    }

    @Test
    public void testConfirmReviewBySessionNotInReviewCardStatus() throws Exception {
        String participant1Username = "participant-1";
        String participant1Password = "participant-1-pass";

        User participant1 = new User(participant1Username, participant1Password);
        participant1.setEmail("test@mail.com");
        participant1 = userService.addUser(participant1);

        Session session = createDefaultSession();
        session.setCardCommentsAllowed(true);
        session.setParticipantsCanAddCards(true);
        session.setMinNumberOfCardsPerParticipant(1);
        session.setMaxNumberOfCardsPerParticipant(2);
        session = sessionService.updateSession(session);

        String tokenParticipant1 = TokenProvider.getToken(mockMvc, clientDetails, participant1Username, participant1Password);
        String authorizationHeaderParticipant1 = String.format("Bearer %s", tokenParticipant1);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite")
                        .header("Authorization", authorizationHeader)
                        .param("email", participant1.getEmail())
        )/*.andDo(MockMvcResultHandlers.print())*/.andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite/confirm")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());
        
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/join")
                        .header("Authorization", authorizationHeaderParticipant1)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/join")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());


        CreateCardDetailsResource createCardDetailsResource = new CreateCardDetailsResource();
        createCardDetailsResource.setText("Added card 1");

        String cardAddedJson = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/all-cards")
                        .header("Authorization", authorizationHeaderParticipant1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new JSONObject(createCardDetailsResource).toString())
        ).andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/all-cards/confirm")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/all-cards/confirm")
                        .header("Authorization", authorizationHeaderParticipant1)
        ).andExpect(MockMvcResultMatchers.status().isCreated());


        Assert.assertEquals(SessionStatus.REVIEWING_CARDS, session.getSessionStatus());

        JSONObject cardAddedObject = new JSONObject(cardAddedJson);

        CreateCardReviewOverview cardReview1 = new CreateCardReviewOverview();
        cardReview1.setCardDetailsId(cardAddedObject.getInt("cardDetailsId"));
        cardReview1.setMessage("card-test-review");

        JSONObject reviewCard1 = new JSONObject(cardReview1);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/reviews")
                        .header("Authorization", authorizationHeaderParticipant1)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(reviewCard1.toString())
        )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(jsonPath("$.commentId").exists())
                .andExpect(jsonPath("$.commentId").isNotEmpty());

        CreateCardReviewOverview cardReview2 = new CreateCardReviewOverview();
        cardReview2.setCardDetailsId(cardAddedObject.getInt("cardDetailsId"));
        cardReview2.setMessage("card-test-review");

        JSONObject reviewCard2 = new JSONObject(cardReview2);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/reviews")
                        .header("Authorization", authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(reviewCard2.toString())
        )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(jsonPath("$.commentId").exists())
                .andExpect(jsonPath("$.commentId").isNotEmpty());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/reviews/confirm")
                        .header("Authorization", authorizationHeaderParticipant1)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/reviews/confirm")
                        .header("Authorization", authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
        ).andExpect(status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/reviews/confirm")
                        .header("Authorization", authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
        ).andExpect(status().isBadRequest());
    }


    @Test
    public void testConfirmReviewByNonUser() throws Exception {
        String participant1Username = "participant-1";
        String participant1Password = "participant-1-pass";

        User participant1 = new User(participant1Username, participant1Password);
        participant1.setEmail("test@mail.com");
        participant1 = userService.addUser(participant1);

        Session session = createDefaultSession();
        session.setCardCommentsAllowed(true);
        session.setParticipantsCanAddCards(true);
        session.setMinNumberOfCardsPerParticipant(1);
        session.setMaxNumberOfCardsPerParticipant(2);
        session = sessionService.updateSession(session);

        String tokenParticipant1 = TokenProvider.getToken(mockMvc, clientDetails, participant1Username, participant1Password);
        String authorizationHeaderParticipant1 = String.format("Bearer %s", tokenParticipant1);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite")
                        .header("Authorization", authorizationHeader)
                        .param("email", participant1.getEmail())
        )/*.andDo(MockMvcResultHandlers.print())*/.andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/invite/confirm")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());
        
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/join")
                        .header("Authorization", authorizationHeaderParticipant1)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/join")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());


        CreateCardDetailsResource createCardDetailsResource = new CreateCardDetailsResource();
        createCardDetailsResource.setText("Added card 1");

        String cardAddedJson = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/all-cards")
                        .header("Authorization", authorizationHeaderParticipant1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new JSONObject(createCardDetailsResource).toString())
        ).andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/all-cards/confirm")
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/all-cards/confirm")
                        .header("Authorization", authorizationHeaderParticipant1)
        ).andExpect(MockMvcResultMatchers.status().isCreated());


        Assert.assertEquals(SessionStatus.REVIEWING_CARDS, session.getSessionStatus());

        JSONObject cardAddedObject = new JSONObject(cardAddedJson);

        CreateCardReviewOverview cardReview1 = new CreateCardReviewOverview();
        cardReview1.setCardDetailsId(cardAddedObject.getInt("cardDetailsId"));
        cardReview1.setMessage("card-test-review");

        JSONObject reviewCard1 = new JSONObject(cardReview1);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/reviews")
                        .header("Authorization", authorizationHeaderParticipant1)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(reviewCard1.toString())
        )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(jsonPath("$.commentId").exists())
                .andExpect(jsonPath("$.commentId").isNotEmpty());

        CreateCardReviewOverview cardReview2 = new CreateCardReviewOverview();
        cardReview2.setCardDetailsId(cardAddedObject.getInt("cardDetailsId"));
        cardReview2.setMessage("card-test-review");

        JSONObject reviewCard2 = new JSONObject(cardReview2);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/reviews")
                        .header("Authorization", authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(reviewCard2.toString())
        )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(jsonPath("$.commentId").exists())
                .andExpect(jsonPath("$.commentId").isNotEmpty());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/reviews/confirm")
                        .header("Authorization", authorizationHeaderParticipant1)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(MockMvcResultMatchers.status().isCreated());

        User nonUser = new User("nonparticipant", "pass");
        userService.addUser(nonUser);

        String tokenNonParticipant = TokenProvider.getToken(mockMvc, clientDetails, nonUser.getUsername(), "pass");
        String authorizationHeaderNonParticipant = String.format("Bearer %s", tokenNonParticipant);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/reviews/confirm")
                        .header("Authorization", authorizationHeaderNonParticipant)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}