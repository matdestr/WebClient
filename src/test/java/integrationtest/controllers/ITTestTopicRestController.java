package integrationtest.controllers;

import be.kdg.kandoe.backend.model.oauth.OAuthClientDetails;
import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.CategoryService;
import be.kdg.kandoe.backend.service.api.OAuthClientDetailsService;
import be.kdg.kandoe.backend.service.api.OrganizationService;
import be.kdg.kandoe.backend.service.api.UserService;
import be.kdg.kandoe.frontend.config.RootContextConfig;
import be.kdg.kandoe.frontend.config.WebContextConfig;
import be.kdg.kandoe.frontend.controller.resources.organizations.topic.CreateTopicResource;
import integrationtest.TokenProvider;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootContextConfig.class, WebContextConfig.class})
@WebAppConfiguration
@Transactional
public class ITTestTopicRestController {

        @Autowired
        private WebApplicationContext webApplicationContext;

        @Autowired
        private FilterChainProxy springSecurityFilterChain;

        @Autowired
        private OAuthClientDetailsService oAuthClientDetailsService;

        @Autowired
        private OrganizationService organizationService;

        @Autowired
        private CategoryService categoryService;

        @Autowired
        private UserService userService;

        private OAuthClientDetails clientDetails;
        private MockMvc mockMvc;

        private String authorizationHeader;

        @Value("/api/topics")
        private String baseApiUrl;

        private Category category1, category2;
        private Organization organization1;


    @Before
        public void setup() throws Exception {
            String unencryptedPassword = "test-password";
            User user = new User("test-user", unencryptedPassword);
            user.setEmail("test@localhost");
            user = userService.addUser(user);

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

            organization1 = new Organization();
            organization1.setName("test-organization-1");
            organization1.setOwner(userService.getUserByUsername(user.getUsername()));

        this.organization1 = organizationService.addOrganization(organization1);

            category1 = new Category();
            category1.setName("test-category-1");
            category1.setOrganization(organization1);

            this.category1 = categoryService.addCategory(category1);

            category2 = new Category();
            category2.setName("test-category-2");
            category2.setOrganization(organization1);

            this.category2 = categoryService.addCategory(category2);

        }


        @Test
        public void testCreateTopicAndGetCreatedTopic() throws Exception {

            CreateTopicResource topicResource = new CreateTopicResource();
            topicResource.setName("test-topic");
            topicResource.setDescription("This is a test topic for test purposes only.");

            JSONObject jsonObject = new JSONObject(topicResource);

            String response = mockMvc.perform(
                    MockMvcRequestBuilders.post(baseApiUrl)
                            .header("Authorization", authorizationHeader)
                            .content(jsonObject.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("categoryId", String.valueOf(category1.getCategoryId())))
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andExpect(jsonPath("$.name", is("test-topic")))
                    .andReturn().getResponse().getContentAsString();

            JSONObject jsonResponse = new JSONObject(response);

            Assert.assertTrue(jsonResponse.getInt("topicId") > 0);

            String getUrl = String.format("%s/%d", baseApiUrl, jsonResponse.getInt("topicId"));

            mockMvc.perform(MockMvcRequestBuilders.get(getUrl)
                    .header("Authorization", authorizationHeader))
                    //.andDo(print())
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(jsonPath("$.topicId").exists())
                    .andExpect(jsonPath("$.topicId").isNotEmpty())
                    .andExpect(jsonPath("$.topicId").isNumber())
                    .andExpect(jsonPath("$.name", is("test-topic")))
                    .andExpect(jsonPath("$.description", is("This is a test topic for test purposes only.")))
                    .andExpect(jsonPath("$.categoryId", is(category1.getCategoryId())));
        }

        @Test
        public void testGetAllTopicsFromOneCategory() throws Exception {
            List<CreateTopicResource> topicResources = new ArrayList<>();

            CreateTopicResource topicResource1 = new CreateTopicResource();
            topicResource1.setName("test-topic-1");
            topicResource1.setDescription("This is a test topic for test purposes only. [1]");

            topicResources.add(topicResource1);

            CreateTopicResource topicResource2 = new CreateTopicResource();
            topicResource2.setName("test-topic-2");
            topicResource2.setDescription("This is a test topic for test purposes only. [2]");

            topicResources.add(topicResource2);

            for (CreateTopicResource topicResource : topicResources) {
                JSONObject jsonObject = new JSONObject(topicResource);

                String response = mockMvc.perform(
                        MockMvcRequestBuilders.post(baseApiUrl)
                                .header("Authorization", authorizationHeader)
                                .content(jsonObject.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("categoryId", String.valueOf(category1.getCategoryId())))
                        .andExpect(MockMvcResultMatchers.status().isCreated())
                        .andReturn().getResponse().getContentAsString();

                JSONObject jsonResponse = new JSONObject(response);

                Assert.assertTrue(jsonResponse.getInt("topicId") > 0);
            }


            CreateTopicResource otherCategoryTopicResource = new CreateTopicResource();
            otherCategoryTopicResource.setName("test-topic-other-category");
            otherCategoryTopicResource.setDescription("This is a test topic for test purposes only. [other-category]");


            JSONObject otherCategoryJsonObject = new JSONObject(otherCategoryTopicResource);

            String response = mockMvc.perform(
                    MockMvcRequestBuilders.post(baseApiUrl)
                            .header("Authorization", authorizationHeader)
                            .content(otherCategoryJsonObject.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("categoryId", String.valueOf(category2.getCategoryId())))
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andReturn().getResponse().getContentAsString();

            JSONObject jsonResponse3 = new JSONObject(response);

            Assert.assertTrue(jsonResponse3.getInt("topicId") > 0);


            mockMvc.perform(MockMvcRequestBuilders.get(baseApiUrl)
                    .header("Authorization", authorizationHeader)
                    .param("categoryId", String.valueOf(category1.getCategoryId())))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(jsonPath("$", hasSize(topicResources.size())))
                    .andExpect(jsonPath("$[0].name", is(topicResource1.getName())))
                    .andExpect(jsonPath("$[0].description", is(topicResource1.getDescription())))
                    .andExpect(jsonPath("$[1].name", is(topicResource2.getName())))
                    .andExpect(jsonPath("$[1].description", is(topicResource2.getDescription())));
        }

}
