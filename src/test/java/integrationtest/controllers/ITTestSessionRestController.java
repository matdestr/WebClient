package integrationtest.controllers;

import be.kdg.kandoe.backend.model.cards.CardDetails;
import be.kdg.kandoe.backend.model.oauth.OAuthClientDetails;
import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.organizations.Topic;
import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.backend.model.sessions.SynchronousSession;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.persistence.api.CardDetailsRepository;
import be.kdg.kandoe.backend.service.api.*;
import be.kdg.kandoe.frontend.config.RootContextConfig;
import be.kdg.kandoe.frontend.config.WebContextConfig;
import be.kdg.kandoe.frontend.controller.resources.cards.CreateCardDetailsResource;
import be.kdg.kandoe.frontend.controller.resources.sessions.create.CreateAsynchronousSessionResource;
import be.kdg.kandoe.frontend.controller.resources.sessions.create.CreateSynchronousSessionResource;
import be.kdg.kandoe.frontend.controller.rest.exceptions.CanDoControllerRuntimeException;
import com.fasterxml.jackson.databind.ObjectMapper;
import integrationtest.IntegrationTestHelpers;
import integrationtest.TokenProvider;
import lombok.val;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootContextConfig.class, WebContextConfig.class})
@WebAppConfiguration
@Transactional
public class ITTestSessionRestController {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private OAuthClientDetailsService oAuthClientDetailsService;

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

    private OAuthClientDetails clientDetails;
    private MockMvc mockMvc;

    private String authorizationHeader;

    @Value("/api/sessions")
    private String baseApiUrl;

    String unencryptedPassword = "test-password";
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
    public void setup() throws Exception {
        this.user = userService.addUser(new User("test-user", unencryptedPassword));

        OAuthClientDetails clientDetails = IntegrationTestHelpers.getOAuthClientDetails();
        this.clientDetails = oAuthClientDetailsService.addClientsDetails(clientDetails);

        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .addFilter(springSecurityFilterChain)
                .build();

        String token = TokenProvider.getToken(mockMvc, clientDetails, user.getUsername(), unencryptedPassword);
        authorizationHeader = String.format("Bearer %s", token);

        this.organization = new Organization("test-organization", user);

        this.category = new Category();
        category.setName("test-category");
        category.setDescription("test-description");
        category.setOrganization(this.organization);

        this.topic = new Topic();
        topic.setName("test-topic");
        topic.setDescription("This is a test topic");
        topic.setCategory(category);

        this.organization = organizationService.addOrganization(organization);
        this.category = categoryService.addCategory(category);
        this.topic = topicService.addTopic(topic);
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

    private JSONObject getSessionData(int sessionId) throws Exception {
        String stringResponse = mockMvc.perform(
                MockMvcRequestBuilders.get(baseApiUrl + "/" + sessionId)
                .header("Authorization", authorizationHeader)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").exists())
                .andExpect(jsonPath("$.sessionId").isNotEmpty())
                .andReturn().getResponse().getContentAsString();
        
        return new JSONObject(stringResponse);
    }

    @Test
    public void testCreateSynchronousSessionOfCategory() throws Exception {
        addCardDetailsToCategory();
        
        CreateSynchronousSessionResource createSynchronousSessionResource = new CreateSynchronousSessionResource();
        createSynchronousSessionResource.setCategoryId(category.getCategoryId());
        createSynchronousSessionResource.setMinNumberOfCardsPerParticipant(3);
        createSynchronousSessionResource.setMaxNumberOfCardsPerParticipant(5);
        createSynchronousSessionResource.setStartDateTime(LocalDateTime.now());

        JSONObject jsonObject = new JSONObject(createSynchronousSessionResource);
        //jsonObject.put("startDateTime", "2016-03-05 20:00");
        jsonObject.put("type", "sync");

        String stringResponse = mockMvc.perform(
                post(baseApiUrl)
                        .header("Authorization", authorizationHeader)
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        )//.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sessionId").exists())
                .andExpect(jsonPath("$.sessionId").isNotEmpty())
                .andReturn().getResponse().getContentAsString();
        
        JSONObject jsonResponse = new JSONObject(stringResponse);
        int createdSessionId = jsonResponse.getInt("sessionId");
        
        JSONObject getResult = getSessionData(createdSessionId);
        JSONArray jsonArrayParticipants = getResult.getJSONArray("participantInfo");
        
        Assert.assertEquals(category.getCategoryId(), getResult.getInt("categoryId"));
        Assert.assertEquals(3, getResult.getInt("minNumberOfCardsPerParticipant"));
        Assert.assertEquals(5, getResult.getInt("maxNumberOfCardsPerParticipant"));
        Assert.assertEquals(1, jsonArrayParticipants.length());
    }

    @Test
    public void testCreateSynchronousSessionWithLessMaxThanMinCards() throws Exception {
        CreateSynchronousSessionResource createSynchronousSessionResource = new CreateSynchronousSessionResource();
        createSynchronousSessionResource.setCategoryId(category.getCategoryId());
        createSynchronousSessionResource.setMinNumberOfCardsPerParticipant(5);
        createSynchronousSessionResource.setMaxNumberOfCardsPerParticipant(4);

        JSONObject jsonObject = new JSONObject(createSynchronousSessionResource);

        mockMvc.perform(
                post(baseApiUrl)
                        .header("Authorization", authorizationHeader)
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void testInviteUserToSession() throws Exception {
        addCardDetailsToCategory();

        CreateSynchronousSessionResource resource = new CreateSynchronousSessionResource();
        resource.setCategoryId(category.getCategoryId());
        resource.setMinNumberOfCardsPerParticipant(3);
        resource.setMaxNumberOfCardsPerParticipant(5);

        JSONObject jsonObject = new JSONObject(resource);
        jsonObject.put("type", "sync");

        String stringResponse = mockMvc.perform(
                post(baseApiUrl)
                        .header("Authorization", authorizationHeader)
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.sessionId").exists())
                .andExpect(jsonPath("$.sessionId").isNotEmpty())
                .andReturn().getResponse().getContentAsString();

        JSONObject jsonResponse = new JSONObject(stringResponse);
        int createdSessionId = jsonResponse.getInt("sessionId");

        JSONObject getResult = getSessionData(createdSessionId);
        JSONArray jsonArrayParticipants = getResult.getJSONArray("participantInfo");
        
        Assert.assertEquals(1, jsonArrayParticipants.length());

        User userToInvite = userService.addUser(new User("participant-1", "pass"));

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/invite")
                        .header("Authorization", authorizationHeader)
                        .param("userId", String.valueOf(userToInvite.getUserId()))
        ).andExpect(status().isCreated());

        getResult = getSessionData(createdSessionId);
        jsonArrayParticipants = getResult.getJSONArray("participantInfo");

        System.out.println("JSON Array : " + jsonArrayParticipants);
        
        Assert.assertEquals(2, jsonArrayParticipants.length());
        
        int matchingUserIdCounter = 0;
        
        for (Object o : jsonArrayParticipants) {
            int id = ((JSONObject) o).getJSONObject("participant").getInt("userId");
            
            if (id == userToInvite.getUserId())
                matchingUserIdCounter++;
        }
        
        Assert.assertEquals(1, matchingUserIdCounter);
    }

    @Test
    public void testInviteDuplicateUserToSession() throws Exception {
        addCardDetailsToCategory();

        CreateSynchronousSessionResource resource = new CreateSynchronousSessionResource();
        resource.setCategoryId(category.getCategoryId());
        resource.setMinNumberOfCardsPerParticipant(3);
        resource.setMaxNumberOfCardsPerParticipant(5);

        JSONObject jsonObject = new JSONObject(resource);
        jsonObject.put("type", "sync");

        String stringResponse = mockMvc.perform(
                post(baseApiUrl)
                        .header("Authorization", authorizationHeader)
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.sessionId").exists())
                .andExpect(jsonPath("$.sessionId").isNotEmpty())
                .andReturn().getResponse().getContentAsString();

        JSONObject jsonResponse = new JSONObject(stringResponse);
        int createdSessionId = jsonResponse.getInt("sessionId");

        JSONObject getResult = getSessionData(createdSessionId);
        JSONArray jsonArrayParticipants = getResult.getJSONArray("participantInfo");

        Assert.assertEquals(1, jsonArrayParticipants.length());

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/invite")
                        .header("Authorization", authorizationHeader)
                        .param("userId", String.valueOf(user.getUserId()))
        ).andExpect(status().isBadRequest());

        getResult = getSessionData(createdSessionId);
        jsonArrayParticipants = getResult.getJSONArray("participantInfo");

        Assert.assertEquals(1, jsonArrayParticipants.length());
    }
    
    @Test
    public void testJoinSessionAsInvitedUser() throws Exception {
        addCardDetailsToCategory();

        CreateSynchronousSessionResource resource = new CreateSynchronousSessionResource();
        resource.setCategoryId(category.getCategoryId());
        resource.setMinNumberOfCardsPerParticipant(3);
        resource.setMaxNumberOfCardsPerParticipant(5);
        resource.setParticipantsCanAddCards(true);

        JSONObject jsonObject = new JSONObject(resource);
        jsonObject.put("type", "sync");

        String createdStringResponse = mockMvc.perform(
                post(baseApiUrl)
                        .header("Authorization", authorizationHeader)
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        
        JSONObject createdJsonResponse = new JSONObject(createdStringResponse);
        int createdSessionId = createdJsonResponse.getInt("sessionId");
        
        JSONObject jsonResponse = this.getSessionData(createdSessionId);
        
        Assert.assertEquals("CREATED", jsonResponse.getString("sessionStatus"));

        User userToInvite = userService.addUser(new User("participant-1", "pass"));

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/invite")
                        .header("Authorization", authorizationHeader)
                        .param("userId", String.valueOf(userToInvite.getUserId()))
        ).andExpect(status().isCreated());
        
        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("USERS_JOINING", jsonResponse.getString("sessionStatus"));
        
        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/join")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
        ).andExpect(status().isCreated());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("USERS_JOINING", jsonResponse.getString("sessionStatus"));

        String token = TokenProvider.getToken(mockMvc, clientDetails, userToInvite.getUsername(), "pass");
        authorizationHeader = String.format("Bearer %s", token);

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/join")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
        ).andExpect(status().isCreated());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("ADDING_CARDS", jsonResponse.getString("sessionStatus"));
    }
    
    @Test
    public void testJoinSessionAsNonInvitedUserResultsInForbidden() throws Exception {
        addCardDetailsToCategory();

        CreateSynchronousSessionResource resource = new CreateSynchronousSessionResource();
        resource.setCategoryId(category.getCategoryId());
        resource.setMinNumberOfCardsPerParticipant(3);
        resource.setMaxNumberOfCardsPerParticipant(5);

        JSONObject jsonObject = new JSONObject(resource);
        jsonObject.put("type", "sync");

        String createdStringResponse = mockMvc.perform(
                post(baseApiUrl)
                        .header("Authorization", authorizationHeader)
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JSONObject createdJsonResponse = new JSONObject(createdStringResponse);
        int createdSessionId = createdJsonResponse.getInt("sessionId");

        JSONObject jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("CREATED", jsonResponse.getString("sessionStatus"));

        User nonInvitedUser = userService.addUser(new User("not-invited-user-1", "pass"));
        
        String token = TokenProvider.getToken(mockMvc, clientDetails, nonInvitedUser.getUsername(), "pass");
        authorizationHeader = String.format("Bearer %s", token);
        
        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/join")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
        ).andDo(print())
                .andExpect(status().isForbidden());

        token = TokenProvider.getToken(mockMvc, clientDetails, user.getUsername(), unencryptedPassword);
        authorizationHeader = String.format("Bearer %s", token);

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("CREATED", jsonResponse.getString("sessionStatus"));
    }

    @Test
    public void testAddCardsToSession() throws Exception {
        addCardDetailsToCategory();

        CreateSynchronousSessionResource resource = new CreateSynchronousSessionResource();
        resource.setCategoryId(category.getCategoryId());
        resource.setMinNumberOfCardsPerParticipant(3);
        resource.setMaxNumberOfCardsPerParticipant(5);
        resource.setParticipantsCanAddCards(true);

        JSONObject jsonObject = new JSONObject(resource);
        jsonObject.put("type", "sync");

        String createdStringResponse = mockMvc.perform(
                post(baseApiUrl)
                        .header("Authorization", authorizationHeader)
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JSONObject createdJsonResponse = new JSONObject(createdStringResponse);
        int createdSessionId = createdJsonResponse.getInt("sessionId");

        JSONObject jsonResponse = this.getSessionData(createdSessionId);

        Assert.assertEquals("CREATED", jsonResponse.getString("sessionStatus"));

        User userToInvite = userService.addUser(new User("participant-1", "pass"));

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/invite")
                        .header("Authorization", authorizationHeader)
                        .param("userId", String.valueOf(userToInvite.getUserId()))
        ).andExpect(status().isCreated());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("USERS_JOINING", jsonResponse.getString("sessionStatus"));

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/join")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
        ).andExpect(status().isCreated());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("USERS_JOINING", jsonResponse.getString("sessionStatus"));

        String token = TokenProvider.getToken(mockMvc, clientDetails, userToInvite.getUsername(), "pass");
        authorizationHeader = String.format("Bearer %s", token);

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/join")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
        ).andExpect(status().isCreated());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("ADDING_CARDS", jsonResponse.getString("sessionStatus"));

        CreateCardDetailsResource createCardDetailsResource = new CreateCardDetailsResource();
        createCardDetailsResource.setText("test-card");

        JSONObject createCardDetailsJson = new JSONObject(createCardDetailsResource);

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/cards")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
                        .content(createCardDetailsJson.toString())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
        ).andExpect(status().isCreated());

        CreateCardDetailsResource createCardDetailsResource2 = new CreateCardDetailsResource();
        createCardDetailsResource2.setText("test-card2");

        JSONObject createCardDetailsJson2 = new JSONObject(createCardDetailsResource2);

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/cards")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
                        .content(createCardDetailsJson2.toString())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
        ).andExpect(status().isCreated());
    }

    @Test
    public void testAddCardsByUserNotInSession() throws Exception {
        addCardDetailsToCategory();

        CreateSynchronousSessionResource resource = new CreateSynchronousSessionResource();
        resource.setCategoryId(category.getCategoryId());
        resource.setMinNumberOfCardsPerParticipant(3);
        resource.setMaxNumberOfCardsPerParticipant(5);
        resource.setParticipantsCanAddCards(true);

        JSONObject jsonObject = new JSONObject(resource);
        jsonObject.put("type", "sync");

        String createdStringResponse = mockMvc.perform(
                post(baseApiUrl)
                        .header("Authorization", authorizationHeader)
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JSONObject createdJsonResponse = new JSONObject(createdStringResponse);
        int createdSessionId = createdJsonResponse.getInt("sessionId");

        JSONObject jsonResponse = this.getSessionData(createdSessionId);

        Assert.assertEquals("CREATED", jsonResponse.getString("sessionStatus"));

        User userToInvite = userService.addUser(new User("participant-1", "pass"));

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/invite")
                        .header("Authorization", authorizationHeader)
                        .param("userId", String.valueOf(userToInvite.getUserId()))
        ).andExpect(status().isCreated());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("USERS_JOINING", jsonResponse.getString("sessionStatus"));

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/join")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
        ).andExpect(status().isCreated());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("USERS_JOINING", jsonResponse.getString("sessionStatus"));

        String token = TokenProvider.getToken(mockMvc, clientDetails, userToInvite.getUsername(), "pass");
        authorizationHeader = String.format("Bearer %s", token);

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/join")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
        ).andExpect(status().isCreated());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("ADDING_CARDS", jsonResponse.getString("sessionStatus"));

        User newUser = userService.addUser(new User("non-participant", "pass"));

        token = TokenProvider.getToken(mockMvc, clientDetails, newUser.getUsername(), "pass");
        authorizationHeader = String.format("Bearer %s", token);

        CreateCardDetailsResource createCardDetailsResource = new CreateCardDetailsResource();
        createCardDetailsResource.setText("test-card");

        JSONObject createCardDetailsJson = new JSONObject(createCardDetailsResource);

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/cards")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
                        .content(createCardDetailsJson.toString())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void testAddCardsInSessionThatDoesntAllowIt() throws Exception {
        addCardDetailsToCategory();

        CreateSynchronousSessionResource resource = new CreateSynchronousSessionResource();
        resource.setCategoryId(category.getCategoryId());
        resource.setMinNumberOfCardsPerParticipant(3);
        resource.setMaxNumberOfCardsPerParticipant(5);
        resource.setParticipantsCanAddCards(false);

        JSONObject jsonObject = new JSONObject(resource);
        jsonObject.put("type", "sync");

        String createdStringResponse = mockMvc.perform(
                post(baseApiUrl)
                        .header("Authorization", authorizationHeader)
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JSONObject createdJsonResponse = new JSONObject(createdStringResponse);
        int createdSessionId = createdJsonResponse.getInt("sessionId");

        JSONObject jsonResponse = this.getSessionData(createdSessionId);

        Assert.assertEquals("CREATED", jsonResponse.getString("sessionStatus"));

        User userToInvite = userService.addUser(new User("participant-1", "pass"));

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/invite")
                        .header("Authorization", authorizationHeader)
                        .param("userId", String.valueOf(userToInvite.getUserId()))
        ).andExpect(status().isCreated());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("USERS_JOINING", jsonResponse.getString("sessionStatus"));

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/join")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
        ).andExpect(status().isCreated());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("USERS_JOINING", jsonResponse.getString("sessionStatus"));

        String token = TokenProvider.getToken(mockMvc, clientDetails, userToInvite.getUsername(), "pass");
        authorizationHeader = String.format("Bearer %s", token);

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/join")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
        ).andExpect(status().isCreated());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("CHOOSING_CARDS", jsonResponse.getString("sessionStatus"));

        CreateCardDetailsResource createCardDetailsResource = new CreateCardDetailsResource();
        createCardDetailsResource.setText("test-card");

        JSONObject createCardDetailsJson = new JSONObject(createCardDetailsResource);

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/cards")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
                        .content(createCardDetailsJson.toString())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void testAddCardsInSessionThatDoesAllowItButIsNotInAddingCardStatus() throws Exception {
        addCardDetailsToCategory();

        CreateSynchronousSessionResource resource = new CreateSynchronousSessionResource();
        resource.setCategoryId(category.getCategoryId());
        resource.setMinNumberOfCardsPerParticipant(3);
        resource.setMaxNumberOfCardsPerParticipant(5);
        resource.setParticipantsCanAddCards(true);
        resource.setCardCommentsAllowed(false);

        JSONObject jsonObject = new JSONObject(resource);
        jsonObject.put("type", "sync");

        String createdStringResponse = mockMvc.perform(
                post(baseApiUrl)
                        .header("Authorization", authorizationHeader)
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JSONObject createdJsonResponse = new JSONObject(createdStringResponse);
        int createdSessionId = createdJsonResponse.getInt("sessionId");

        JSONObject jsonResponse = this.getSessionData(createdSessionId);

        Assert.assertEquals("CREATED", jsonResponse.getString("sessionStatus"));

        User userToInvite = userService.addUser(new User("participant-1", "pass"));

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/invite")
                        .header("Authorization", authorizationHeader)
                        .param("userId", String.valueOf(userToInvite.getUserId()))
        ).andExpect(status().isCreated());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("USERS_JOINING", jsonResponse.getString("sessionStatus"));

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/join")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
        ).andExpect(status().isCreated());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("USERS_JOINING", jsonResponse.getString("sessionStatus"));

        String token = TokenProvider.getToken(mockMvc, clientDetails, userToInvite.getUsername(), "pass");
        authorizationHeader = String.format("Bearer %s", token);

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/join")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
        ).andExpect(status().isCreated());

        CreateCardDetailsResource createCardDetailsResource = new CreateCardDetailsResource();
        createCardDetailsResource.setText("test-card1");

        JSONObject createCardDetailsJson = new JSONObject(createCardDetailsResource);

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/cards")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
                        .content(createCardDetailsJson.toString())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
        ).andExpect(status().isCreated());

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/cards")
                        .header("Authorization", authorizationHeader)
                        .content(createCardDetailsJson.toString())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
        ).andExpect(status().isCreated());

        token = TokenProvider.getToken(mockMvc, clientDetails, user.getUsername(), unencryptedPassword);
        authorizationHeader = String.format("Bearer %s", token);

        mockMvc.perform(
                put(baseApiUrl + "/" + createdSessionId + "/cards" )
                        .header("Authorization", authorizationHeader)
        ).andExpect(status().isOk());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("READY_TO_START", jsonResponse.getString("sessionStatus"));

        CreateCardDetailsResource createCardDetailsResource2 = new CreateCardDetailsResource();
        createCardDetailsResource2.setText("test-card2");

        JSONObject createCardDetailsJson2 = new JSONObject(createCardDetailsResource2);

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/cards")
                        .header("Authorization", authorizationHeader)
                        .content(createCardDetailsJson2.toString())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void testSetCardAddingDone() throws Exception {
        addCardDetailsToCategory();

        CreateSynchronousSessionResource resource = new CreateSynchronousSessionResource();
        resource.setCategoryId(category.getCategoryId());
        resource.setMinNumberOfCardsPerParticipant(3);
        resource.setMaxNumberOfCardsPerParticipant(5);
        resource.setParticipantsCanAddCards(true);
        resource.setCardCommentsAllowed(false);

        JSONObject jsonObject = new JSONObject(resource);
        jsonObject.put("type", "sync");

        String createdStringResponse = mockMvc.perform(
                post(baseApiUrl)
                        .header("Authorization", authorizationHeader)
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JSONObject createdJsonResponse = new JSONObject(createdStringResponse);
        int createdSessionId = createdJsonResponse.getInt("sessionId");

        JSONObject jsonResponse = this.getSessionData(createdSessionId);

        Assert.assertEquals("CREATED", jsonResponse.getString("sessionStatus"));

        User userToInvite = userService.addUser(new User("participant-1", "pass"));

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/invite")
                        .header("Authorization", authorizationHeader)
                        .param("userId", String.valueOf(userToInvite.getUserId()))
        ).andExpect(status().isCreated());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("USERS_JOINING", jsonResponse.getString("sessionStatus"));

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/join")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
        ).andExpect(status().isCreated());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("USERS_JOINING", jsonResponse.getString("sessionStatus"));

        String token = TokenProvider.getToken(mockMvc, clientDetails, userToInvite.getUsername(), "pass");
        authorizationHeader = String.format("Bearer %s", token);

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/join")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
        ).andExpect(status().isCreated());

        CreateCardDetailsResource createCardDetailsResource = new CreateCardDetailsResource();
        createCardDetailsResource.setText("test-card1");

        JSONObject createCardDetailsJson = new JSONObject(createCardDetailsResource);

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/cards")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
                        .content(createCardDetailsJson.toString())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
        ).andExpect(status().isCreated());

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/cards")
                        .header("Authorization", authorizationHeader)
                        .content(createCardDetailsJson.toString())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
        ).andExpect(status().isCreated());

        token = TokenProvider.getToken(mockMvc, clientDetails, user.getUsername(), unencryptedPassword);
        authorizationHeader = String.format("Bearer %s", token);

        mockMvc.perform(
                put(baseApiUrl + "/" + createdSessionId + "/cards" )
                        .header("Authorization", authorizationHeader)
        ).andExpect(status().isOk());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("READY_TO_START", jsonResponse.getString("sessionStatus"));
    }

    @Test
    public void testSetCardAddingDoneByNotOrganizer() throws Exception {
        addCardDetailsToCategory();

        CreateSynchronousSessionResource resource = new CreateSynchronousSessionResource();
        resource.setCategoryId(category.getCategoryId());
        resource.setMinNumberOfCardsPerParticipant(3);
        resource.setMaxNumberOfCardsPerParticipant(5);
        resource.setParticipantsCanAddCards(true);
        resource.setCardCommentsAllowed(false);

        JSONObject jsonObject = new JSONObject(resource);
        jsonObject.put("type", "sync");

        String createdStringResponse = mockMvc.perform(
                post(baseApiUrl)
                        .header("Authorization", authorizationHeader)
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JSONObject createdJsonResponse = new JSONObject(createdStringResponse);
        int createdSessionId = createdJsonResponse.getInt("sessionId");

        JSONObject jsonResponse = this.getSessionData(createdSessionId);

        Assert.assertEquals("CREATED", jsonResponse.getString("sessionStatus"));

        User userToInvite = userService.addUser(new User("participant-1", "pass"));

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/invite")
                        .header("Authorization", authorizationHeader)
                        .param("userId", String.valueOf(userToInvite.getUserId()))
        ).andExpect(status().isCreated());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("USERS_JOINING", jsonResponse.getString("sessionStatus"));

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/join")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
        ).andExpect(status().isCreated());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("USERS_JOINING", jsonResponse.getString("sessionStatus"));

        String token = TokenProvider.getToken(mockMvc, clientDetails, userToInvite.getUsername(), "pass");
        authorizationHeader = String.format("Bearer %s", token);

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/join")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
        ).andExpect(status().isCreated());

        CreateCardDetailsResource createCardDetailsResource = new CreateCardDetailsResource();
        createCardDetailsResource.setText("test-card1");

        JSONObject createCardDetailsJson = new JSONObject(createCardDetailsResource);

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/cards")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
                        .content(createCardDetailsJson.toString())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
        ).andExpect(status().isCreated());

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/cards")
                        .header("Authorization", authorizationHeader)
                        .content(createCardDetailsJson.toString())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
        ).andExpect(status().isCreated());

        mockMvc.perform(
                put(baseApiUrl + "/" + createdSessionId + "/cards" )
                        .header("Authorization", authorizationHeader)
        ).andExpect(status().isConflict());
    }

    @Test
    public void testSetCardsReviewingDone() throws Exception {
        addCardDetailsToCategory();

        CreateSynchronousSessionResource resource = new CreateSynchronousSessionResource();
        resource.setCategoryId(category.getCategoryId());
        resource.setMinNumberOfCardsPerParticipant(3);
        resource.setMaxNumberOfCardsPerParticipant(5);
        resource.setParticipantsCanAddCards(false);
        resource.setCardCommentsAllowed(true);

        JSONObject jsonObject = new JSONObject(resource);
        jsonObject.put("type", "sync");

        String createdStringResponse = mockMvc.perform(
                post(baseApiUrl)
                        .header("Authorization", authorizationHeader)
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JSONObject createdJsonResponse = new JSONObject(createdStringResponse);
        int createdSessionId = createdJsonResponse.getInt("sessionId");

        JSONObject jsonResponse = this.getSessionData(createdSessionId);

        Assert.assertEquals("CREATED", jsonResponse.getString("sessionStatus"));

        User userToInvite = userService.addUser(new User("participant-1", "pass"));

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/invite")
                        .header("Authorization", authorizationHeader)
                        .param("userId", String.valueOf(userToInvite.getUserId()))
        ).andExpect(status().isCreated());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("USERS_JOINING", jsonResponse.getString("sessionStatus"));

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/join")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
        ).andExpect(status().isCreated());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("USERS_JOINING", jsonResponse.getString("sessionStatus"));

        String token = TokenProvider.getToken(mockMvc, clientDetails, userToInvite.getUsername(), "pass");
        authorizationHeader = String.format("Bearer %s", token);

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/join")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
        ).andExpect(status().isCreated());

        token = TokenProvider.getToken(mockMvc, clientDetails, user.getUsername(), unencryptedPassword);
        authorizationHeader = String.format("Bearer %s", token);

        mockMvc.perform(
                put(baseApiUrl + "/" + createdSessionId + "/reviews" )
                        .header("Authorization", authorizationHeader)
        ).andExpect(status().isOk());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("READY_TO_START", jsonResponse.getString("sessionStatus"));
    }

    @Test
    public void testSetCardsReviewingDoneByNonOrganizer() throws Exception {
        addCardDetailsToCategory();

        CreateSynchronousSessionResource resource = new CreateSynchronousSessionResource();
        resource.setCategoryId(category.getCategoryId());
        resource.setMinNumberOfCardsPerParticipant(3);
        resource.setMaxNumberOfCardsPerParticipant(5);
        resource.setParticipantsCanAddCards(false);
        resource.setCardCommentsAllowed(true);

        JSONObject jsonObject = new JSONObject(resource);
        jsonObject.put("type", "sync");

        String createdStringResponse = mockMvc.perform(
                post(baseApiUrl)
                        .header("Authorization", authorizationHeader)
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JSONObject createdJsonResponse = new JSONObject(createdStringResponse);
        int createdSessionId = createdJsonResponse.getInt("sessionId");

        JSONObject jsonResponse = this.getSessionData(createdSessionId);

        Assert.assertEquals("CREATED", jsonResponse.getString("sessionStatus"));

        User userToInvite = userService.addUser(new User("participant-1", "pass"));

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/invite")
                        .header("Authorization", authorizationHeader)
                        .param("userId", String.valueOf(userToInvite.getUserId()))
        ).andExpect(status().isCreated());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("USERS_JOINING", jsonResponse.getString("sessionStatus"));

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/join")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
        ).andExpect(status().isCreated());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("USERS_JOINING", jsonResponse.getString("sessionStatus"));

        String token = TokenProvider.getToken(mockMvc, clientDetails, userToInvite.getUsername(), "pass");
        authorizationHeader = String.format("Bearer %s", token);

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/join")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
        ).andExpect(status().isCreated());

        token = TokenProvider.getToken(mockMvc, clientDetails, userToInvite.getUsername(), "pass");
        authorizationHeader = String.format("Bearer %s", token);

        mockMvc.perform(
                put(baseApiUrl + "/" + createdSessionId + "/reviews" )
                        .header("Authorization", authorizationHeader)
        ).andExpect(status().isConflict());
    }

    @Test
    public void testChooseCardsStatus() throws Exception {
        addCardDetailsToCategory();

        CreateSynchronousSessionResource resource = new CreateSynchronousSessionResource();
        resource.setCategoryId(category.getCategoryId());
        resource.setMinNumberOfCardsPerParticipant(3);
        resource.setMaxNumberOfCardsPerParticipant(5);
        resource.setParticipantsCanAddCards(true);

        JSONObject jsonObject = new JSONObject(resource);
        jsonObject.put("type", "sync");

        String createdStringResponse = mockMvc.perform(
                post(baseApiUrl)
                        .header("Authorization", authorizationHeader)
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JSONObject createdJsonResponse = new JSONObject(createdStringResponse);
        int createdSessionId = createdJsonResponse.getInt("sessionId");

        JSONObject jsonResponse = this.getSessionData(createdSessionId);

        Assert.assertEquals("CREATED", jsonResponse.getString("sessionStatus"));

        User userToInvite = userService.addUser(new User("participant-1", "pass"));

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/invite")
                        .header("Authorization", authorizationHeader)
                        .param("userId", String.valueOf(userToInvite.getUserId()))
        ).andExpect(status().isCreated());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("USERS_JOINING", jsonResponse.getString("sessionStatus"));

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/join")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
        ).andExpect(status().isCreated());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("USERS_JOINING", jsonResponse.getString("sessionStatus"));

        String token = TokenProvider.getToken(mockMvc, clientDetails, userToInvite.getUsername(), "pass");
        authorizationHeader = String.format("Bearer %s", token);

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/join")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
        ).andExpect(status().isCreated());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("ADDING_CARDS", jsonResponse.getString("sessionStatus"));

        CreateCardDetailsResource createCardDetailsResource = new CreateCardDetailsResource();
        createCardDetailsResource.setText("test-card");

        JSONObject createCardDetailsJson = new JSONObject(createCardDetailsResource);

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/cards")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
                        .content(createCardDetailsJson.toString())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
        ).andExpect(status().isCreated());

        CreateCardDetailsResource createCardDetailsResource2 = new CreateCardDetailsResource();
        createCardDetailsResource2.setText("test-card2");

        JSONObject createCardDetailsJson2 = new JSONObject(createCardDetailsResource2);

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/cards")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
                        .content(createCardDetailsJson2.toString())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
        ).andExpect(status().isCreated());

        token = TokenProvider.getToken(mockMvc, clientDetails, user.getUsername(), unencryptedPassword);
        authorizationHeader = String.format("Bearer %s", token);

        mockMvc.perform(
                put(baseApiUrl + "/" + createdSessionId + "/cards" )
                        .header("Authorization", authorizationHeader)
        ).andExpect(status().isOk());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("CHOOSING_CARDS", jsonResponse.getString("sessionStatus"));
    }

    @Test
    public void testChooseCardsDuringChooseCardsStatus() throws Exception {
        addCardDetailsToCategory();

        CreateSynchronousSessionResource resource = new CreateSynchronousSessionResource();
        resource.setCategoryId(category.getCategoryId());
        resource.setMinNumberOfCardsPerParticipant(3);
        resource.setMaxNumberOfCardsPerParticipant(5);
        resource.setParticipantsCanAddCards(true);

        JSONObject jsonObject = new JSONObject(resource);
        jsonObject.put("type", "sync");

        String createdStringResponse = mockMvc.perform(
                post(baseApiUrl)
                        .header("Authorization", authorizationHeader)
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JSONObject createdJsonResponse = new JSONObject(createdStringResponse);
        int createdSessionId = createdJsonResponse.getInt("sessionId");

        JSONObject jsonResponse = this.getSessionData(createdSessionId);

        Assert.assertEquals("CREATED", jsonResponse.getString("sessionStatus"));

        User userToInvite = userService.addUser(new User("participant-1", "pass"));

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/invite")
                        .header("Authorization", authorizationHeader)
                        .param("userId", String.valueOf(userToInvite.getUserId()))
        ).andExpect(status().isCreated());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("USERS_JOINING", jsonResponse.getString("sessionStatus"));

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/join")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
        ).andExpect(status().isCreated());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("USERS_JOINING", jsonResponse.getString("sessionStatus"));

        String token = TokenProvider.getToken(mockMvc, clientDetails, userToInvite.getUsername(), "pass");
        authorizationHeader = String.format("Bearer %s", token);

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/join")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
        ).andExpect(status().isCreated());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("ADDING_CARDS", jsonResponse.getString("sessionStatus"));

        CreateCardDetailsResource createCardDetailsResource = new CreateCardDetailsResource();
        createCardDetailsResource.setText("test-card");

        JSONObject createCardDetailsJson = new JSONObject(createCardDetailsResource);

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/cards")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
                        .content(createCardDetailsJson.toString())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
        ).andExpect(status().isCreated());


        token = TokenProvider.getToken(mockMvc, clientDetails, user.getUsername(), unencryptedPassword);
        authorizationHeader = String.format("Bearer %s", token);

        mockMvc.perform(
                put(baseApiUrl + "/" + createdSessionId + "/cards" )
                        .header("Authorization", authorizationHeader)
        ).andExpect(status().isOk());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("CHOOSING_CARDS", jsonResponse.getString("sessionStatus"));

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/" + "cards" + "/" + cardDetails1.getCardDetailsId() )
                        .header("Authorization", authorizationHeader)
        ).andExpect(status().isCreated());

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/" + "cards" + "/" + cardDetails2.getCardDetailsId() )
                        .header("Authorization", authorizationHeader)
        ).andExpect(status().isCreated());
    }

    @Test
    public void testChooseCardsByUserNotInSession() throws Exception {
        addCardDetailsToCategory();

        CreateSynchronousSessionResource resource = new CreateSynchronousSessionResource();
        resource.setCategoryId(category.getCategoryId());
        resource.setMinNumberOfCardsPerParticipant(3);
        resource.setMaxNumberOfCardsPerParticipant(5);
        resource.setParticipantsCanAddCards(true);

        JSONObject jsonObject = new JSONObject(resource);
        jsonObject.put("type", "sync");

        String createdStringResponse = mockMvc.perform(
                post(baseApiUrl)
                        .header("Authorization", authorizationHeader)
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JSONObject createdJsonResponse = new JSONObject(createdStringResponse);
        int createdSessionId = createdJsonResponse.getInt("sessionId");

        JSONObject jsonResponse = this.getSessionData(createdSessionId);

        Assert.assertEquals("CREATED", jsonResponse.getString("sessionStatus"));

        User userToInvite = userService.addUser(new User("participant-1", "pass"));

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/invite")
                        .header("Authorization", authorizationHeader)
                        .param("userId", String.valueOf(userToInvite.getUserId()))
        ).andExpect(status().isCreated());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("USERS_JOINING", jsonResponse.getString("sessionStatus"));

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/join")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
        ).andExpect(status().isCreated());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("USERS_JOINING", jsonResponse.getString("sessionStatus"));

        String token = TokenProvider.getToken(mockMvc, clientDetails, userToInvite.getUsername(), "pass");
        authorizationHeader = String.format("Bearer %s", token);

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/join")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
        ).andExpect(status().isCreated());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("ADDING_CARDS", jsonResponse.getString("sessionStatus"));

        CreateCardDetailsResource createCardDetailsResource = new CreateCardDetailsResource();
        createCardDetailsResource.setText("test-card");

        JSONObject createCardDetailsJson = new JSONObject(createCardDetailsResource);

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/cards")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
                        .content(createCardDetailsJson.toString())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
        ).andExpect(status().isCreated());


        token = TokenProvider.getToken(mockMvc, clientDetails, user.getUsername(), unencryptedPassword);
        authorizationHeader = String.format("Bearer %s", token);

        mockMvc.perform(
                put(baseApiUrl + "/" + createdSessionId + "/cards" )
                        .header("Authorization", authorizationHeader)
        ).andExpect(status().isOk());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("CHOOSING_CARDS", jsonResponse.getString("sessionStatus"));

        User newUser = userService.addUser(new User("non-participant", "pass"));

        token = TokenProvider.getToken(mockMvc, clientDetails, newUser.getUsername(), "pass");
        authorizationHeader = String.format("Bearer %s", token);

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/" + "cards" + "/" + cardDetails1.getCardDetailsId() )
                        .header("Authorization", authorizationHeader)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void testChooseCardsWithNonExistingCardInCategoryOrTopic() throws Exception {
        addCardDetailsToCategory();

        CreateSynchronousSessionResource resource = new CreateSynchronousSessionResource();
        resource.setCategoryId(category.getCategoryId());
        resource.setMinNumberOfCardsPerParticipant(3);
        resource.setMaxNumberOfCardsPerParticipant(5);
        resource.setParticipantsCanAddCards(true);

        JSONObject jsonObject = new JSONObject(resource);
        jsonObject.put("type", "sync");

        String createdStringResponse = mockMvc.perform(
                post(baseApiUrl)
                        .header("Authorization", authorizationHeader)
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JSONObject createdJsonResponse = new JSONObject(createdStringResponse);
        int createdSessionId = createdJsonResponse.getInt("sessionId");

        JSONObject jsonResponse = this.getSessionData(createdSessionId);

        Assert.assertEquals("CREATED", jsonResponse.getString("sessionStatus"));

        User userToInvite = userService.addUser(new User("participant-1", "pass"));

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/invite")
                        .header("Authorization", authorizationHeader)
                        .param("userId", String.valueOf(userToInvite.getUserId()))
        ).andExpect(status().isCreated());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("USERS_JOINING", jsonResponse.getString("sessionStatus"));

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/join")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
        ).andExpect(status().isCreated());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("USERS_JOINING", jsonResponse.getString("sessionStatus"));

        String token = TokenProvider.getToken(mockMvc, clientDetails, userToInvite.getUsername(), "pass");
        authorizationHeader = String.format("Bearer %s", token);

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/join")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
        ).andExpect(status().isCreated());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("ADDING_CARDS", jsonResponse.getString("sessionStatus"));

        CreateCardDetailsResource createCardDetailsResource = new CreateCardDetailsResource();
        createCardDetailsResource.setText("test-card");

        JSONObject createCardDetailsJson = new JSONObject(createCardDetailsResource);

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/cards")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
                        .content(createCardDetailsJson.toString())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
        ).andExpect(status().isCreated());


        token = TokenProvider.getToken(mockMvc, clientDetails, user.getUsername(), unencryptedPassword);
        authorizationHeader = String.format("Bearer %s", token);

        mockMvc.perform(
                put(baseApiUrl + "/" + createdSessionId + "/cards" )
                        .header("Authorization", authorizationHeader)
        ).andExpect(status().isOk());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("CHOOSING_CARDS", jsonResponse.getString("sessionStatus"));

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/" + "cards" + "/" + -1 )
                        .header("Authorization", authorizationHeader)
        ).andExpect(status().isBadRequest());
    }

    @Test
    public void testChooseCardsTooManyCards() throws Exception {
        addCardDetailsToCategory();

        CreateSynchronousSessionResource resource = new CreateSynchronousSessionResource();
        resource.setCategoryId(category.getCategoryId());
        resource.setMinNumberOfCardsPerParticipant(1);
        resource.setMaxNumberOfCardsPerParticipant(1);
        resource.setParticipantsCanAddCards(true);

        JSONObject jsonObject = new JSONObject(resource);
        jsonObject.put("type", "sync");

        String createdStringResponse = mockMvc.perform(
                post(baseApiUrl)
                        .header("Authorization", authorizationHeader)
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JSONObject createdJsonResponse = new JSONObject(createdStringResponse);
        int createdSessionId = createdJsonResponse.getInt("sessionId");

        JSONObject jsonResponse = this.getSessionData(createdSessionId);

        Assert.assertEquals("CREATED", jsonResponse.getString("sessionStatus"));

        User userToInvite = userService.addUser(new User("participant-1", "pass"));

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/invite")
                        .header("Authorization", authorizationHeader)
                        .param("userId", String.valueOf(userToInvite.getUserId()))
        ).andExpect(status().isCreated());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("USERS_JOINING", jsonResponse.getString("sessionStatus"));

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/join")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
        ).andExpect(status().isCreated());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("USERS_JOINING", jsonResponse.getString("sessionStatus"));

        String token = TokenProvider.getToken(mockMvc, clientDetails, userToInvite.getUsername(), "pass");
        authorizationHeader = String.format("Bearer %s", token);

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/join")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
        ).andExpect(status().isCreated());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("ADDING_CARDS", jsonResponse.getString("sessionStatus"));

        CreateCardDetailsResource createCardDetailsResource = new CreateCardDetailsResource();
        createCardDetailsResource.setText("test-card");

        JSONObject createCardDetailsJson = new JSONObject(createCardDetailsResource);

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/cards")
                        .header("Authorization", authorizationHeader)
                        .param("sessionId", String.valueOf(createdSessionId))
                        .content(createCardDetailsJson.toString())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
        ).andExpect(status().isCreated());


        token = TokenProvider.getToken(mockMvc, clientDetails, user.getUsername(), unencryptedPassword);
        authorizationHeader = String.format("Bearer %s", token);

        mockMvc.perform(
                put(baseApiUrl + "/" + createdSessionId + "/cards" )
                        .header("Authorization", authorizationHeader)
        ).andExpect(status().isOk());

        jsonResponse = this.getSessionData(createdSessionId);
        Assert.assertEquals("CHOOSING_CARDS", jsonResponse.getString("sessionStatus"));

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/" + "cards" + "/" + cardDetails1.getCardDetailsId() )
                        .header("Authorization", authorizationHeader)
        ).andExpect(status().isCreated());

        mockMvc.perform(
                post(baseApiUrl + "/" + createdSessionId + "/" + "cards" + "/" + cardDetails2.getCardDetailsId() )
                        .header("Authorization", authorizationHeader)
        ).andExpect(status().isBadRequest()).andDo(print());
    }
}