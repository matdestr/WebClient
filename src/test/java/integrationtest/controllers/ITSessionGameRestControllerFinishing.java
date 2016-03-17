package integrationtest.controllers;

import be.kdg.kandoe.backend.model.cards.CardDetails;
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
import integrationtest.IntegrationTestHelpers;
import integrationtest.TokenProvider;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.HashSet;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootContextConfig.class, WebContextConfig.class})
@WebAppConfiguration
@Transactional
public class ITSessionGameRestControllerFinishing {
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

    private String sessionOrganizerUsername = "test-user-1";
    private String sessionOrganizerPassword = "test-user-1";
    
    private String participant1Username = "participant-1";
    private String participant1Password = "participant-1";
    
    private String nonParticipantUsername = "non-participant";
    private String nonParticipantPassword = "non-participant";

    private User sessionOrganizer;
    private User participant1;
    private User nonParticipant;
    private Organization organization;
    private Category category;
    private Topic topic;
    private Session session;

    private CardDetails cardDetails1;
    private CardDetails cardDetails2;
    private CardDetails cardDetails3;
    private CardDetails cardDetails4;
    private CardDetails cardDetails5;

    private OAuthClientDetails clientDetails;
    private String authorizationHeaderSessionOrganizer;
    private String authorizationHeaderParticipant1;
    private String authorizationHeaderNonParticipant;
    private MockMvc mockMvc;
    
    @Before
    public void setup() throws Exception {
        this.sessionOrganizer = new User(sessionOrganizerUsername, sessionOrganizerPassword);
        this.sessionOrganizer.setEmail("test-user-1@localhost");
        this.sessionOrganizer = userService.addUser(sessionOrganizer);
        
        this.participant1 = new User(participant1Username, participant1Password);
        this.participant1.setEmail("participant-1@localhost.com");
        this.participant1 = userService.addUser(participant1);
        
        this.nonParticipant = new User(nonParticipantUsername, nonParticipantPassword);
        this.nonParticipant.setEmail("non-participant@localhost.com");
        this.nonParticipant = userService.addUser(nonParticipant);

        OAuthClientDetails clientDetails = IntegrationTestHelpers.getOAuthClientDetails();
        this.clientDetails = oAuthClientDetailsService.addClientsDetails(clientDetails);

        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .addFilter(springSecurityFilterChain)
                .build();

        String token = TokenProvider.getToken(mockMvc, clientDetails, sessionOrganizerUsername, sessionOrganizerPassword);
        authorizationHeaderSessionOrganizer = String.format("Bearer %s", token);
        
        token = TokenProvider.getToken(mockMvc, clientDetails, participant1Username, participant1Password);
        authorizationHeaderParticipant1 = String.format("Bearer %s", token);
        
        token = TokenProvider.getToken(mockMvc, clientDetails, nonParticipantUsername, nonParticipantPassword);
        authorizationHeaderNonParticipant = String.format("Bearer %s", token);
        
        setSessionInProgress();
    }
    
    private void setSessionInProgress() {
        organization = new Organization("test-organization-1", sessionOrganizer);
        organization = organizationService.addOrganization(organization);

        category = new Category();
        category.setName("test-category-1");
        category.setDescription("Description of test category 1");
        category.setOrganization(organization);
        category = categoryService.addCategory(category);

        cardDetails1 = new CardDetails();
        cardDetails1.setCategory(category);
        cardDetails1.setCreator(sessionOrganizer);
        cardDetails1.setText("Card 1");

        cardDetails2 = new CardDetails();
        cardDetails2.setCategory(category);
        cardDetails2.setCreator(sessionOrganizer);
        cardDetails2.setText("Card 2");

        cardDetails3 = new CardDetails();
        cardDetails3.setCategory(category);
        cardDetails3.setCreator(sessionOrganizer);
        cardDetails3.setText("Card 3");

        cardDetails4 = new CardDetails();
        cardDetails4.setCategory(category);
        cardDetails4.setCreator(sessionOrganizer);
        cardDetails4.setText("Card 4");

        cardDetails5 = new CardDetails();
        cardDetails5.setCategory(category);
        cardDetails5.setCreator(sessionOrganizer);
        cardDetails5.setText("Card 5");

        cardDetails1 = cardService.addCardDetailsToCategory(category, cardDetails1);
        cardDetails2 = cardService.addCardDetailsToCategory(category, cardDetails2);
        cardDetails3 = cardService.addCardDetailsToCategory(category, cardDetails3);
        cardDetails4 = cardService.addCardDetailsToCategory(category, cardDetails4);
        cardDetails5 = cardService.addCardDetailsToCategory(category, cardDetails5);
        
        Session syncSession = new SynchronousSession();
        syncSession.setOrganizer(sessionOrganizer);
        syncSession.setCategory(category);
        syncSession.setAmountOfCircles(4);
        syncSession.setParticipantsCanAddCards(false);
        syncSession.setCardCommentsAllowed(false);
        syncSession.setMinNumberOfCardsPerParticipant(2);
        syncSession.setMaxNumberOfCardsPerParticipant(3);
        
        this.session = sessionService.addSession(syncSession);
        
        sessionGameService.inviteUserForSession(session, participant1);
        sessionGameService.confirmInvitedUsers(session);
        
        sessionGameService.setUserJoined(session, sessionOrganizer);
        sessionGameService.setUserJoined(session, participant1);

        Set<CardDetails> cardChoicesSessionOrganizer = new HashSet<>();
        Set<CardDetails> cardChoicesParticipant1 = new HashSet<>();
        
        cardChoicesSessionOrganizer.add(cardDetails1);
        cardChoicesSessionOrganizer.add(cardDetails2);
        
        cardChoicesParticipant1.add(cardDetails2);
        cardChoicesParticipant1.add(cardDetails3);
        cardChoicesParticipant1.add(cardDetails4);
        
        sessionGameService.chooseCards(session, sessionOrganizer, cardChoicesSessionOrganizer);
        sessionGameService.chooseCards(session, participant1, cardChoicesParticipant1);

        sessionGameService.startGame(session);
    }
    
    @Test
    public void endSessionWithSingleWinningCard() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/sessions/" + session.getSessionId() + "/positions")
                    .header("Authorization", authorizationHeaderSessionOrganizer)
                    .param("cardDetailsId", String.valueOf(cardDetails1.getCardDetailsId()))
        ).andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/sessions/" + session.getSessionId() + "/positions")
                        .header("Authorization", authorizationHeaderParticipant1)
                        .param("cardDetailsId", String.valueOf(cardDetails1.getCardDetailsId()))
        ).andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/sessions/" + session.getSessionId() + "/positions")
                        .header("Authorization", authorizationHeaderSessionOrganizer)
                        .param("cardDetailsId", String.valueOf(cardDetails1.getCardDetailsId()))
        ).andExpect(MockMvcResultMatchers.status().isOk());
        
        session = sessionService.getSessionById(session.getSessionId());
        
        Assert.assertEquals(SessionStatus.FINISHED, session.getSessionStatus());
        Assert.assertNotNull(session.getWinners());
        Assert.assertEquals(1, session.getWinners().size());
        Assert.assertEquals(cardDetails1, session.getWinners().get(0));
    }
    
    @Test
    public void endSessionManuallyWithSingleWinningCard() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/sessions/" + session.getSessionId() + "/positions")
                        .header("Authorization", authorizationHeaderSessionOrganizer)
                        .param("cardDetailsId", String.valueOf(cardDetails1.getCardDetailsId()))
        ).andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/sessions/" + session.getSessionId() + "/positions")
                        .header("Authorization", authorizationHeaderParticipant1)
                        .param("cardDetailsId", String.valueOf(cardDetails1.getCardDetailsId()))
        ).andExpect(MockMvcResultMatchers.status().isOk());

        session = sessionService.getSessionById(session.getSessionId());
        Assert.assertEquals(SessionStatus.IN_PROGRESS, session.getSessionStatus());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/end")
                        .header("Authorization", authorizationHeaderSessionOrganizer)
        ).andExpect(MockMvcResultMatchers.status().isOk());

        session = sessionService.getSessionById(session.getSessionId());
        Assert.assertEquals(SessionStatus.FINISHED, session.getSessionStatus());
        
        Assert.assertNotNull(session.getWinners());
        Assert.assertEquals(1, session.getWinners().size());
        Assert.assertEquals(cardDetails1, session.getWinners().get(0));
        
        String stringResponseWinningCards = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/sessions/" + session.getSessionId() + "/winning-cards")
                    .header("Authorization", authorizationHeaderParticipant1)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andReturn().getResponse().getContentAsString();

        JSONArray jsonArrayWinningCards = new JSONArray(stringResponseWinningCards);
        
        Assert.assertEquals(1, jsonArrayWinningCards.length());
        Assert.assertEquals(cardDetails1.getCardDetailsId(), jsonArrayWinningCards.getJSONObject(0).getInt("cardDetailsId"));
    }
    
    @Test
    public void endSessionWithTwoWinningCards() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/sessions/" + session.getSessionId() + "/positions")
                        .header("Authorization", authorizationHeaderSessionOrganizer)
                        .param("cardDetailsId", String.valueOf(cardDetails1.getCardDetailsId()))
        ).andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/sessions/" + session.getSessionId() + "/positions")
                        .header("Authorization", authorizationHeaderParticipant1)
                        .param("cardDetailsId", String.valueOf(cardDetails1.getCardDetailsId()))
        ).andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/sessions/" + session.getSessionId() + "/positions")
                        .header("Authorization", authorizationHeaderSessionOrganizer)
                        .param("cardDetailsId", String.valueOf(cardDetails2.getCardDetailsId()))
        ).andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/sessions/" + session.getSessionId() + "/positions")
                        .header("Authorization", authorizationHeaderParticipant1)
                        .param("cardDetailsId", String.valueOf(cardDetails2.getCardDetailsId()))
        ).andExpect(MockMvcResultMatchers.status().isOk());

        session = sessionService.getSessionById(session.getSessionId());
        Assert.assertEquals(SessionStatus.IN_PROGRESS, session.getSessionStatus());
        
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/end")
                        .header("Authorization", authorizationHeaderSessionOrganizer)
        ).andExpect(MockMvcResultMatchers.status().isOk());

        session = sessionService.getSessionById(session.getSessionId());
        Assert.assertEquals(SessionStatus.FINISHED, session.getSessionStatus());

        Assert.assertNotNull(session.getWinners());
        Assert.assertEquals(2, session.getWinners().size());
        Assert.assertTrue(session.getWinners().contains(cardDetails1));
        Assert.assertTrue(session.getWinners().contains(cardDetails2));

        String stringResponseWinningCards = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/sessions/" + session.getSessionId() + "/winning-cards")
                        .header("Authorization", authorizationHeaderParticipant1)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andReturn().getResponse().getContentAsString();

        JSONArray jsonArrayWinningCards = new JSONArray(stringResponseWinningCards);

        Assert.assertEquals(2, jsonArrayWinningCards.length());
        
        boolean jsonArrayContainsFirstCard = false;
        boolean jsonArrayContainsSecondCard = false;

        for (Object nextObject : jsonArrayWinningCards) {
            if (((JSONObject) nextObject).getInt("cardDetailsId") == cardDetails1.getCardDetailsId()) {
                jsonArrayContainsFirstCard = true;
            }

            if (((JSONObject) nextObject).getInt("cardDetailsId") == cardDetails2.getCardDetailsId()) {
                jsonArrayContainsSecondCard = true;
            }
        }
        
        Assert.assertTrue(jsonArrayContainsFirstCard);
        Assert.assertTrue(jsonArrayContainsSecondCard);
    }
    
    @Test
    public void getWinningCardsAsNonParticipantResultsInForbidden() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/sessions/" + session.getSessionId() + "/positions")
                        .header("Authorization", authorizationHeaderSessionOrganizer)
                        .param("cardDetailsId", String.valueOf(cardDetails1.getCardDetailsId()))
        ).andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/sessions/" + session.getSessionId() + "/positions")
                        .header("Authorization", authorizationHeaderParticipant1)
                        .param("cardDetailsId", String.valueOf(cardDetails1.getCardDetailsId()))
        ).andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + session.getSessionId() + "/end")
                        .header("Authorization", authorizationHeaderSessionOrganizer)
        ).andExpect(MockMvcResultMatchers.status().isOk());

        session = sessionService.getSessionById(session.getSessionId());
        Assert.assertEquals(SessionStatus.FINISHED, session.getSessionStatus());

        Assert.assertNotNull(session.getWinners());
        Assert.assertEquals(1, session.getWinners().size());
        Assert.assertEquals(cardDetails1, session.getWinners().get(0));

        String stringResponseWinningCards = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/sessions/" + session.getSessionId() + "/winning-cards")
                        .header("Authorization", authorizationHeaderParticipant1)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andReturn().getResponse().getContentAsString();

        JSONArray jsonArrayWinningCards = new JSONArray(stringResponseWinningCards);

        Assert.assertEquals(1, jsonArrayWinningCards.length());
        Assert.assertEquals(cardDetails1.getCardDetailsId(), jsonArrayWinningCards.getJSONObject(0).getInt("cardDetailsId"));

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/sessions/" + session.getSessionId() + "/winning-cards")
                        .header("Authorization", authorizationHeaderNonParticipant)
        ).andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}
