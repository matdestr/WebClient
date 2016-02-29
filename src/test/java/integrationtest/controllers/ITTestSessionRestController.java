package integrationtest.controllers;

import be.kdg.kandoe.backend.model.oauth.OAuthClientDetails;
import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.organizations.Topic;
import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.backend.model.sessions.SynchronousSession;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.*;
import be.kdg.kandoe.frontend.config.RootContextConfig;
import be.kdg.kandoe.frontend.config.WebContextConfig;
import be.kdg.kandoe.frontend.controller.resources.sessions.create.CreateAsynchronousSessionResource;
import be.kdg.kandoe.frontend.controller.resources.sessions.create.CreateSynchronousSessionResource;
import be.kdg.kandoe.frontend.controller.rest.exceptions.CanDoControllerRuntimeException;
import com.fasterxml.jackson.databind.ObjectMapper;
import integrationtest.TokenProvider;
import org.json.JSONObject;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    private OAuthClientDetails clientDetails;
    private MockMvc mockMvc;

    private String authorizationHeader;

    @Value("/api/sessions")
    private String baseApiUrl;

    private User user;
    private Organization organization;
    private Category category;
    private Topic topic;

    @Before
    public void setup() throws Exception {
        String unencryptedPassword = "test-password";
        this.user = userService.addUser(new User("test-user", unencryptedPassword));

        OAuthClientDetails newClientDetails = new OAuthClientDetails("test-client-id");
        newClientDetails.setAuthorizedGrandTypes("password", "refresh_token");
        newClientDetails.setAuthorities("ROLE_TEST_CLIENT");
        newClientDetails.setScopes("read");
        newClientDetails.setSecret("secret");

        this.clientDetails = oAuthClientDetailsService.addClientsDetails(newClientDetails);

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

        organizationService.addOrganization(organization);
        categoryService.addCategory(category);
        topicService.addTopic(topic);
    }

    @Test
    public void testCreateAsynchronousSession() throws Exception {
        CreateAsynchronousSessionResource createAsynchSessionResource = new CreateAsynchronousSessionResource();
        createAsynchSessionResource.setOrganizationId(organization.getOrganizationId());
        createAsynchSessionResource.setMaxNumberOfCards(10);
        createAsynchSessionResource.setMinNumberOfCards(5);
        createAsynchSessionResource.setCommentsAllowed(true);
        JSONObject jsonObject = new JSONObject(createAsynchSessionResource);

        mockMvc.perform(
                post(baseApiUrl)
                        .header("Authorization", authorizationHeader)
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated())
                //.andDo(print())
                .andExpect(jsonPath("$.sessionId").exists())
                .andExpect(jsonPath("$.sessionId").isNotEmpty());
    }

    @Test
    public void testCreateSynchronousSession() throws Exception {
        CreateSynchronousSessionResource createSynchronousSessionResource = new CreateSynchronousSessionResource();
        createSynchronousSessionResource.setOrganizationId(organization.getOrganizationId());
        createSynchronousSessionResource.setMinNumberOfCards(5);
        createSynchronousSessionResource.setMaxNumberOfCards(10);

        JSONObject jsonObject = new JSONObject(createSynchronousSessionResource);

        mockMvc.perform(
                post(baseApiUrl)
                        .header("Authorization", authorizationHeader)
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        )/*.andDo(print())*/.andExpect(status().isCreated())
                .andExpect(jsonPath("$.sessionId").exists())
                .andExpect(jsonPath("$.sessionId").isNotEmpty());
    }

    @Test
    public void testCreateSynchronousSessionWithLessMaxThanMinCards() throws Exception {
        CreateSynchronousSessionResource createSynchronousSessionResource = new CreateSynchronousSessionResource();
        createSynchronousSessionResource.setOrganizationId(organization.getOrganizationId());
        createSynchronousSessionResource.setMinNumberOfCards(10);
        createSynchronousSessionResource.setMaxNumberOfCards(2);

        JSONObject jsonObject = new JSONObject(createSynchronousSessionResource);

        mockMvc.perform(
                post(baseApiUrl)
                        .header("Authorization", authorizationHeader)
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void testCreateAsynchronousSessionWithLessMaxThanMinCards() throws Exception {
        CreateAsynchronousSessionResource createAsynchSessionResource = new CreateAsynchronousSessionResource();
        createAsynchSessionResource.setOrganizationId(organization.getOrganizationId());
        createAsynchSessionResource.setMaxNumberOfCards(5);
        createAsynchSessionResource.setMinNumberOfCards(10);
        createAsynchSessionResource.setCommentsAllowed(true);
        JSONObject jsonObject = new JSONObject(createAsynchSessionResource);

        mockMvc.perform(
                post(baseApiUrl)
                        .header("Authorization", authorizationHeader)
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void testCreateAsynchronousSessionWithoutAuthorization() throws Exception {
        CreateAsynchronousSessionResource createAsynchSessionResource = new CreateAsynchronousSessionResource();
        createAsynchSessionResource.setOrganizationId(organization.getOrganizationId());
        createAsynchSessionResource.setMaxNumberOfCards(10);
        createAsynchSessionResource.setMinNumberOfCards(5);
        createAsynchSessionResource.setCommentsAllowed(true);

        JSONObject jsonObject = new JSONObject(createAsynchSessionResource);

        mockMvc.perform(
                post(baseApiUrl)
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    public void testCreateSynchronousSessionWithTopic() throws Exception {
        CreateSynchronousSessionResource createSynchronousSessionResource = new CreateSynchronousSessionResource();
        createSynchronousSessionResource.setOrganizationId(organization.getOrganizationId());
        createSynchronousSessionResource.setMinNumberOfCards(5);
        createSynchronousSessionResource.setMaxNumberOfCards(10);
        createSynchronousSessionResource.setTopicId(topic.getTopicId());

        JSONObject jsonObject = new JSONObject(createSynchronousSessionResource);

        mockMvc.perform(
                post(baseApiUrl)
                        .header("Authorization", authorizationHeader)
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        )/*.andDo(print())*/.andExpect(status().isCreated())
                .andExpect(jsonPath("$.sessionId").exists())
                .andExpect(jsonPath("$.sessionId").isNotEmpty())
                .andExpect(jsonPath("$.topicId", is(topic.getTopicId())));
    }

    @Test
    public void testCreateAsynchronousSessionWithTopic() throws Exception {
        CreateAsynchronousSessionResource createAsynchSessionResource = new CreateAsynchronousSessionResource();
        createAsynchSessionResource.setOrganizationId(organization.getOrganizationId());
        createAsynchSessionResource.setMaxNumberOfCards(10);
        createAsynchSessionResource.setMinNumberOfCards(5);
        createAsynchSessionResource.setCommentsAllowed(true);
        createAsynchSessionResource.setTopicId(topic.getTopicId());
        JSONObject jsonObject = new JSONObject(createAsynchSessionResource);

        mockMvc.perform(
                post(baseApiUrl)
                        .header("Authorization", authorizationHeader)
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        )/*.andDo(print())*/.andExpect(status().isCreated())
                .andExpect(jsonPath("$.sessionId").exists())
                .andExpect(jsonPath("$.sessionId").isNotEmpty())
                .andExpect(jsonPath("$.topicId", is(topic.getTopicId())));
    }

    @Test
    public void testAddUserToSynchronousSession() throws Exception {
        User userToAdd = new User("add", "pass");
        userToAdd = userService.addUser(userToAdd);

        CreateSynchronousSessionResource resource = new CreateSynchronousSessionResource();
        resource.setOrganizationId(organization.getOrganizationId());
        resource.setMinNumberOfCards(5);
        resource.setMaxNumberOfCards(10);
        resource.setTopicId(topic.getTopicId());

        JSONObject jsonObject = new JSONObject(resource);

        MvcResult result = mockMvc.perform(
                post(baseApiUrl)
                        .header("Authorization", authorizationHeader)
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();

        String jsonResponseString = result.getResponse().getContentAsString();
        JSONObject jsonResponse = new JSONObject(jsonResponseString);
        int sessionId = (int) jsonResponse.get("sessionId");

        String url = String.format(baseApiUrl + "/add/%d?userId=%d", sessionId, userToAdd.getUserId());

        mockMvc.perform(post(url).header("Authorization", authorizationHeader)).andExpect(status().isOk());
    }



    @Test
    public void testAddUserToAsynchronousSession() throws Exception {
        User userToAdd = new User("add", "pass");
        userToAdd = userService.addUser(userToAdd);

        CreateAsynchronousSessionResource resource = new CreateAsynchronousSessionResource();
        resource.setOrganizationId(organization.getOrganizationId());
        resource.setMinNumberOfCards(5);
        resource.setMaxNumberOfCards(10);
        resource.setTopicId(topic.getTopicId());

        JSONObject jsonObject = new JSONObject(resource);

        MvcResult result = mockMvc.perform(
                post(baseApiUrl)
                        .header("Authorization", authorizationHeader)
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();

        String jsonResponseString = result.getResponse().getContentAsString();
        JSONObject jsonResponse = new JSONObject(jsonResponseString);
        int sessionId = (int) jsonResponse.get("sessionId");

        String url = String.format(baseApiUrl + "/add/%d?userId=%d", sessionId, userToAdd.getUserId());

        mockMvc.perform(post(url).header("Authorization", authorizationHeader)).andExpect(status().isOk());
    }

    @Test
    public void testAddUserWithInvalidIdToAsynchronousSession() throws Exception {
        CreateSynchronousSessionResource resource = new CreateSynchronousSessionResource();
        resource.setOrganizationId(organization.getOrganizationId());
        resource.setMinNumberOfCards(5);
        resource.setMaxNumberOfCards(10);
        resource.setTopicId(topic.getTopicId());


        JSONObject jsonObject = new JSONObject(resource);

        MvcResult result = mockMvc.perform(
                post(baseApiUrl)
                        .header("Authorization", authorizationHeader)
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();

        String jsonResponseString = result.getResponse().getContentAsString();
        JSONObject jsonResponse = new JSONObject(jsonResponseString);
        int sessionId = (int) jsonResponse.get("sessionId");

        String url = String.format(baseApiUrl + "/add/%d?userId=%d", sessionId, -1);

        mockMvc.perform(post(url).header("Authorization", authorizationHeader)).andExpect(status().isBadRequest());
    }

}