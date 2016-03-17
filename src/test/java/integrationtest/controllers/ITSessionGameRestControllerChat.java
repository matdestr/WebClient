package integrationtest.controllers;

import be.kdg.kandoe.backend.model.cards.CardDetails;
import be.kdg.kandoe.backend.model.oauth.OAuthClientDetails;
import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.sessions.ChatMessage;
import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.backend.model.sessions.SynchronousSession;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.*;
import be.kdg.kandoe.frontend.config.RootContextConfig;
import be.kdg.kandoe.frontend.config.WebContextConfig;
import integrationtest.IntegrationTestHelpers;
import integrationtest.TokenProvider;
import org.json.JSONArray;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootContextConfig.class, WebContextConfig.class})
@WebAppConfiguration
@Transactional
public class ITSessionGameRestControllerChat {
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Autowired
    private FilterChainProxy springSecurityFilterChain;
    
    @Autowired
    private OAuthClientDetailsService oAuthClientDetailsService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private OrganizationService organizationService;
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private TopicService topicService;
    
    @Autowired
    private CardService cardService;
    
    @Autowired
    private SessionService sessionService;
    
    @Autowired
    private SessionGameService sessionGameService;
    
    private OAuthClientDetails clientDetails;
    private String authorizationHeaderSessionOwner;
    private String authorizationHeaderParticipant1;
    
    private String sessionOwnerUsername = "test-session-owner";
    private String sessionOwnerPassword = "test-session-pass";
    
    private String participant1Username = "test-participant-1";
    private String participant1Password = "test-participant-pass";
    
    private User sessionOwner;
    private User participant1;
    
    private Organization defaultOrganization;
    private Category defaultCategory;
    private Session defaultSession;
    
    private CardDetails cardDetails1;
    private CardDetails cardDetails2;
    private CardDetails cardDetails3;
    
    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        User organizer = new User(sessionOwnerUsername, sessionOwnerPassword);
        organizer.setEmail("cando-session-owner@localhost.com");
        
        User participant = new User(participant1Username, participant1Password);
        participant.setEmail("cando-session-participant-1@localhost.com");
        
        this.sessionOwner = userService.addUser(organizer);
        this.participant1 = userService.addUser(participant);

        OAuthClientDetails clientDetails = IntegrationTestHelpers.getOAuthClientDetails();
        this.clientDetails = oAuthClientDetailsService.addClientsDetails(clientDetails);

        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .addFilter(springSecurityFilterChain)
                .build();

        String token = TokenProvider.getToken(mockMvc, clientDetails, sessionOwnerUsername, sessionOwnerPassword);
        authorizationHeaderSessionOwner = String.format("Bearer %s", token);

        token = TokenProvider.getToken(mockMvc, clientDetails, participant1Username, participant1Password);
        authorizationHeaderParticipant1 = String.format("Bearer %s", token);
        
        createOrganization();
        createCategory();
        addCardDetailsToCategory();
        createSession();
        inviteParticipantAndStartSession();
    }
    
    private void createOrganization() {
        Organization organization = new Organization("Test Organization 1", sessionOwner);
        this.defaultOrganization = organizationService.addOrganization(organization);
    }
    
    private void createCategory() {
        Category category = new Category();
        
        category.setOrganization(this.defaultOrganization);
        category.setName("Test Category 1");
        category.setDescription("This category was made for testing purposes");
        
        this.defaultCategory = categoryService.addCategory(category);
    }
    
    private void addCardDetailsToCategory() {
        cardDetails1 = new CardDetails();
        cardDetails2 = new CardDetails();
        cardDetails3 = new CardDetails();
        
        cardDetails1.setCategory(this.defaultCategory);
        cardDetails1.setCreator(this.sessionOwner);
        cardDetails1.setText("Card 1");

        cardDetails2.setCategory(this.defaultCategory);
        cardDetails2.setCreator(this.sessionOwner);
        cardDetails2.setText("Card 2");

        cardDetails3.setCategory(this.defaultCategory);
        cardDetails3.setCreator(this.sessionOwner);
        cardDetails3.setText("Card 3");
        
        cardDetails1 = cardService.addCardDetailsToCategory(this.defaultCategory, cardDetails1);
        cardDetails2 = cardService.addCardDetailsToCategory(this.defaultCategory, cardDetails2);
        cardDetails3 = cardService.addCardDetailsToCategory(this.defaultCategory, cardDetails3);
    }
    
    private void createSession() {
        Session session = new SynchronousSession();
        
        session.setCategory(this.defaultCategory);
        session.setOrganizer(this.sessionOwner);
        session.setCardCommentsAllowed(false);
        session.setParticipantsCanAddCards(false);
        session.setMinNumberOfCardsPerParticipant(2);
        session.setMaxNumberOfCardsPerParticipant(3);
        
        this.defaultSession = sessionService.addSession(session);
    }
    
    private void inviteParticipantAndStartSession() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + defaultSession.getSessionId() + "/invite")
                        .header("Authorization", authorizationHeaderSessionOwner)
                        .param("email", String.valueOf(participant1.getEmail()))
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + defaultSession.getSessionId() + "/invite/confirm")
                        .header("Authorization", authorizationHeaderSessionOwner)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + defaultSession.getSessionId() + "/join")
                        .header("Authorization", authorizationHeaderSessionOwner)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + defaultSession.getSessionId() + "/join")
                        .header("Authorization", authorizationHeaderParticipant1)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + defaultSession.getSessionId() + "/chosen-cards")
                        .param("cardDetailsId", String.valueOf(cardDetails1.getCardDetailsId()))
                        .param("cardDetailsId", String.valueOf(cardDetails3.getCardDetailsId()))
                        .header("Authorization", authorizationHeaderSessionOwner)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + defaultSession.getSessionId() + "/chosen-cards")
                        .param("cardDetailsId", String.valueOf(cardDetails2.getCardDetailsId()))
                        .param("cardDetailsId", String.valueOf(cardDetails3.getCardDetailsId()))
                        .header("Authorization", authorizationHeaderParticipant1)
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/sessions/" + defaultSession.getSessionId() + "/start")
                        .header("Authorization", authorizationHeaderSessionOwner)
        ).andExpect(MockMvcResultMatchers.status().isCreated());
    }
    
    @Test
    public void getChatMessagesOfSessionAsOrganizer() throws Exception {
        String chatMessagesStringResponse = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/sessions/" + this.defaultSession.getSessionId() + "/chat")
                    .header("Authorization", authorizationHeaderSessionOwner)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        JSONArray chatMessagesJsonArray = new JSONArray(chatMessagesStringResponse);
        Assert.assertEquals(0, chatMessagesJsonArray.length());

        ChatMessage chatMessage1 = new ChatMessage();
        chatMessage1.setSession(defaultSession);
        chatMessage1.setUser(sessionOwner);
        chatMessage1.setContent("First testing chat message");
        chatMessage1.setDateTime(LocalDateTime.now());

        this.defaultSession.getChatMessages().add(chatMessage1);
        this.defaultSession = sessionService.updateSession(defaultSession);

        chatMessagesStringResponse = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/sessions/" + this.defaultSession.getSessionId() + "/chat")
                        .header("Authorization", authorizationHeaderSessionOwner)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andReturn().getResponse().getContentAsString();

        chatMessagesJsonArray = new JSONArray(chatMessagesStringResponse);
        
        Assert.assertEquals(1, chatMessagesJsonArray.length());
        Assert.assertEquals(sessionOwner.getUserId(), chatMessagesJsonArray.getJSONObject(0).getJSONObject("user").getInt("userId"));
        Assert.assertEquals(chatMessage1.getContent(), chatMessagesJsonArray.getJSONObject(0).getString("content"));
    }
    
    @Test
    public void getChatMessagesOfSessionAsParticipant() throws Exception {
        String chatMessagesStringResponse = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/sessions/" + this.defaultSession.getSessionId() + "/chat")
                        .header("Authorization", authorizationHeaderParticipant1)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        JSONArray chatMessagesJsonArray = new JSONArray(chatMessagesStringResponse);
        Assert.assertEquals(0, chatMessagesJsonArray.length());

        ChatMessage chatMessage1 = new ChatMessage();
        chatMessage1.setSession(defaultSession);
        chatMessage1.setUser(sessionOwner);
        chatMessage1.setContent("First testing chat message");
        chatMessage1.setDateTime(LocalDateTime.now());

        this.defaultSession.getChatMessages().add(chatMessage1);
        this.defaultSession = sessionService.updateSession(defaultSession);
        
        this.defaultSession = sessionService.getSessionById(this.defaultSession.getSessionId());

        chatMessagesStringResponse = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/sessions/" + this.defaultSession.getSessionId() + "/chat")
                        .header("Authorization", authorizationHeaderParticipant1)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andReturn().getResponse().getContentAsString();

        chatMessagesJsonArray = new JSONArray(chatMessagesStringResponse);

        Assert.assertEquals(1, chatMessagesJsonArray.length());
        Assert.assertEquals(sessionOwner.getUserId(), chatMessagesJsonArray.getJSONObject(0).getJSONObject("user").getInt("userId"));
        Assert.assertEquals(chatMessage1.getContent(), chatMessagesJsonArray.getJSONObject(0).getString("content"));
    }
    
    @Test
    public void getChatMessagesAsNonParticipantResultsInForbidden() throws Exception {
        String nonParticipantUsername = "test-non-participant";
        String nonParticipantPassword = "pass";
        
        User nonParticipant =new User(nonParticipantUsername, nonParticipantPassword);
        nonParticipant.setEmail("test-user@localhost");
        userService.addUser(nonParticipant);

        String token = TokenProvider.getToken(mockMvc, clientDetails, nonParticipantUsername, nonParticipantPassword);
        String authorizationHeaderNonParticipant = String.format("Bearer %s", token);
        
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/sessions/" + this.defaultSession.getSessionId() + "/chat")
                        .header("Authorization", authorizationHeaderNonParticipant)
        ).andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}
