package integrationtest.controllers;

import be.kdg.kandoe.backend.model.oauth.OAuthClientDetails;
import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.organizations.Topic;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.*;
import be.kdg.kandoe.frontend.config.RootContextConfig;
import be.kdg.kandoe.frontend.config.WebContextConfig;
import be.kdg.kandoe.frontend.controller.resources.cards.CreateCardDetailsResource;
import integrationtest.IntegrationTestHelpers;
import integrationtest.TokenProvider;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
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

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootContextConfig.class, WebContextConfig.class})
@WebAppConfiguration
@Transactional
public class ITCardDetailsRestController {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private UserService userService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private OAuthClientDetailsService oAuthClientDetailsService;

    private OAuthClientDetails clientDetails;

    private MockMvc mockMvc;
    private OAuthClientDetails oAuthClientDetails;

    String username = "test-user";
    String password = "pass";

    private User user;
    private String token;
    private String authorizationHeader;

    private Organization organization;
    private Category category;
    private Topic topic;

    @Before
    public void setup() throws Exception {
        User user = new User(username, password);
        this.user = this.userService.addUser(user);

        OAuthClientDetails clientDetails = IntegrationTestHelpers.getOAuthClientDetails();
        this.clientDetails = oAuthClientDetailsService.addClientsDetails(clientDetails);

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(springSecurityFilterChain)
                .build();

        this.token = TokenProvider.getToken(mockMvc, clientDetails, username, password);
        authorizationHeader = String.format("Bearer %s", token);

        initializeNeededData();
    }

    private void initializeNeededData() {
        Organization organization = new Organization("test-organization", user);
        this.organization = this.organizationService.addOrganization(organization);

        Category category = new Category();
        category.setName("test-category");
        category.setDescription("test-description");
        category.setOrganization(organization);
        this.category = this.categoryService.addCategory(category);

        Topic topic = new Topic();
        topic.setName("test-topic");
        topic.setDescription("test-description");
        topic.setCategory(category);
        this.topic = this.topicService.addTopic(topic);
    }

    @Test
    public void createNewCardDetailsAsOrganizationOwner() throws Exception {
        String text = "My first card";

        CreateCardDetailsResource resource = new CreateCardDetailsResource();
        resource.setText(text);

        JSONObject jsonObject = new JSONObject(resource);

        String response = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/carddetails/categories")
                        .param("categoryId", String.valueOf(category.getCategoryId()))
                        .header("Authorization", authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonObject.toString())
        ).andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.text", Is.is(text)))
                .andReturn().getResponse().getContentAsString();

        JSONObject jsonResponse = new JSONObject(response);

        Assert.assertTrue(jsonResponse.getInt("cardDetailsId") > 0);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/carddetails/topics")
                        .param("topicId", String.valueOf(topic.getTopicId()))
                        .param("cardDetailsId", String.valueOf(jsonResponse.getInt("cardDetailsId")))
                        .header("Authorization", authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonObject.toString())
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        //String getUrl = String.format("/api/carddetails/%d", jsonResponse.getInt("cardDetailsId"));

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/carddetails/topics/" + String.valueOf(topic.getTopicId()))
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].cardDetailsId").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].cardDetailsId").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].text").value(text))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)));

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/carddetails/categories/" + String.valueOf(category.getCategoryId()))
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].cardDetailsId").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].cardDetailsId").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].text").value(text));
    }

    @Test
    public void createNewCardDetailsAsOrganizer() throws Exception {
        User organizer = new User("test-organizer", "pass");
        organizer = this.userService.addUser(organizer);

        this.token = TokenProvider.getToken(mockMvc, clientDetails, organizer.getUsername(), "pass");
        authorizationHeader = String.format("Bearer %s", token);

        Organization org = this.organization;
        List<User> organizers = new ArrayList<>();
        organizers.add(organizer);
        org.setOrganizers(organizers);
        this.organizationService.updateOrganization(org);

        String text = "My second card";

        CreateCardDetailsResource resource = new CreateCardDetailsResource();
        resource.setText(text);

        JSONObject jsonObject = new JSONObject(resource);

        String response = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/carddetails/categories")
                        .param("categoryId", String.valueOf(category.getCategoryId()))
                        .header("Authorization", authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonObject.toString())
        ).andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.text", Is.is(text)))
                .andReturn().getResponse().getContentAsString();

        JSONObject jsonResponse = new JSONObject(response);

        Assert.assertTrue(jsonResponse.getInt("cardDetailsId") > 0);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/carddetails/topics")
                        .param("topicId", String.valueOf(topic.getTopicId()))
                        .param("cardDetailsId", String.valueOf(jsonResponse.getInt("cardDetailsId")))
                        .header("Authorization", authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonObject.toString())
        ).andExpect(MockMvcResultMatchers.status().isCreated());
        
        //String getUrl = String.format("/api/carddetails/%d", jsonResponse.getInt("cardDetailsId"));

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/carddetails/topics/" + String.valueOf(topic.getTopicId()))
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].cardDetailsId").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].cardDetailsId").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].text").value(text));
    }

    @Test
    public void createNewCardDetailsAsNonOrganizer() throws Exception {
        User user = new User("test-non-organizer", "pass");
        user = this.userService.addUser(user);

        this.token = TokenProvider.getToken(mockMvc, clientDetails, user.getUsername(), "pass");
        authorizationHeader = String.format("Bearer %s", token);

        String text = "Card details text";

        CreateCardDetailsResource resource = new CreateCardDetailsResource();
        resource.setText(text);

        JSONObject jsonObject = new JSONObject(resource);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/carddetails/categories")
                        .param("categoryId", String.valueOf(category.getCategoryId()))
                        .header("Authorization", authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonObject.toString())
        ).andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void createNewCardDetailsInvalidImageUrl() throws Exception {
        String text = "Card details text";
        String imageUrl = "http://www.google.com";

        CreateCardDetailsResource resource = new CreateCardDetailsResource();
        resource.setText(text);
        resource.setImageUrl(imageUrl);

        JSONObject jsonObject = new JSONObject(resource);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/carddetails/categories")
                        .param("categoryId", String.valueOf(topic.getTopicId()))
                        .header("Authorization", authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonObject.toString())
        ).andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());

        String jsonResponse = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/carddetails/topics" + String.valueOf(this.topic.getTopicId()))
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        Assert.assertEquals(0, jsonResponse.length());
    }
    
    @Test
    public void addCardDetailsWithValidImageUrl() throws Exception {
        String text = "Card details text";
        String imageUrl = "https://i.ytimg.com/vi/8FF2JvHau2w/maxresdefault.jpg";

        CreateCardDetailsResource resource = new CreateCardDetailsResource();
        resource.setText(text);
        resource.setImageUrl(imageUrl);

        JSONObject jsonObject = new JSONObject(resource);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/carddetails/categories")
                        .param("categoryId", String.valueOf(category.getCategoryId()))
                        .header("Authorization", authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonObject.toString())
        ).andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/carddetails/categories/" + String.valueOf(this.category.getCategoryId()))
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].cardDetailsId").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].cardDetailsId").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].text").value(text))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].imageUrl").value(imageUrl));
    }

    @Test
    public void addExistingCardDetailsToTopic() throws Exception {
        String text = "Card details text";

        CreateCardDetailsResource resource = new CreateCardDetailsResource();
        resource.setText(text);

        JSONObject jsonObject = new JSONObject(resource);

        String jsonResponse = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/carddetails/categories")
                        .param("categoryId", String.valueOf(category.getCategoryId()))
                        .header("Authorization", authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonObject.toString())
        ).andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JSONObject jsonResponseObject = new JSONObject(jsonResponse);
        int cardDetailsId = jsonResponseObject.getInt("cardDetailsId");

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/carddetails/topics")
                        .param("topicId", String.valueOf(topic.getTopicId()))
                        .param("cardDetailsId", String.valueOf(cardDetailsId))
                        .header("Authorization", authorizationHeader))
                        .andExpect(MockMvcResultMatchers.status().isCreated());


        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/carddetails/topics/" + String.valueOf(topic.getTopicId()))
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andReturn().getResponse().getContentAsString();
    }
    
    @Test
    public void addNonExistingCardDetailsToTopic() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/carddetails/topics")
                        .param("topicId", String.valueOf(topic.getTopicId()))
                        .param("cardDetailsId", String.valueOf(999))
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest());
        
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/carddetails/topics/" + String.valueOf(topic.getTopicId()))
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty())
                .andReturn().getResponse().getContentAsString();
    }
    
    @Test
    public void addCardDetailsToNonExistingTopic() throws Exception {
        String text = "Card details text";

        CreateCardDetailsResource resource = new CreateCardDetailsResource();
        resource.setText(text);

        JSONObject jsonObject = new JSONObject(resource);

        String jsonResponse = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/carddetails/categories")
                        .param("categoryId", String.valueOf(category.getCategoryId()))
                        .header("Authorization", authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonObject.toString())
        ).andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JSONObject jsonResponseObject = new JSONObject(jsonResponse);
        int cardDetailsId = jsonResponseObject.getInt("cardDetailsId");

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/carddetails/topics")
                        .param("topicId", String.valueOf(999))
                        .param("cardDetailsId", String.valueOf(cardDetailsId))
                        .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void getCardsOfNonExistingCategoryIsBadRequest() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/carddetails/categories/-1")
                    .header("Authorization", authorizationHeader)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
