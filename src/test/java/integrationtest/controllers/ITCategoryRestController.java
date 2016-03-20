package integrationtest.controllers;

import be.kdg.kandoe.backend.model.oauth.OAuthClientDetails;
import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.OAuthClientDetailsService;
import be.kdg.kandoe.backend.service.api.OrganizationService;
import be.kdg.kandoe.backend.service.api.UserService;
import be.kdg.kandoe.frontend.config.RootContextConfig;
import be.kdg.kandoe.frontend.config.WebContextConfig;
import be.kdg.kandoe.frontend.controller.resources.organizations.categories.CreateCategoryResource;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootContextConfig.class, WebContextConfig.class})
@WebAppConfiguration
@Transactional
public class ITCategoryRestController {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private OAuthClientDetailsService oAuthClientDetailsService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private UserService userService;

    private OAuthClientDetails clientDetails;
    private MockMvc mockMvc;

    private String authorizationHeader;

    @Value("/api/categories")
    private String baseApiUrl;

    private Organization organization1, organization2;

    @Before
    public void setup() throws Exception {
        String unencryptedPassword = "test-password";
        User user = new User("test-user", unencryptedPassword);
        user.setEmail("test-user@localhost");
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

        Organization organization1 = new Organization();
        organization1.setName("test-organization-1");
        organization1.setOwner(userService.getUserByUsername(user.getUsername()));

        this.organization1 = organizationService.addOrganization(organization1);

        Organization organization2 = new Organization();
        organization2.setName("test-organization-2");
        organization2.setOwner(userService.getUserByUsername(user.getUsername()));

        this.organization2 = organizationService.addOrganization(organization2);

    }


    @Test
    public void testCreateCategoryAndGetCreatedCategory() throws Exception {
        CreateCategoryResource categoryResource = new CreateCategoryResource();
        categoryResource.setName("test-category");
        categoryResource.setDescription("This is a test category for test purposes only.");

        JSONObject jsonObject = new JSONObject(categoryResource);

        String response = mockMvc.perform(
                MockMvcRequestBuilders.post(baseApiUrl)
                        .header("Authorization", authorizationHeader)
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("organizationId", String.valueOf(organization1.getOrganizationId())))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(jsonPath("$.name", is("test-category")))
                .andReturn().getResponse().getContentAsString();

        JSONObject jsonResponse = new JSONObject(response);

        Assert.assertTrue(jsonResponse.getInt("categoryId") > 0);

        String getUrl = String.format("%s/%d", baseApiUrl, jsonResponse.getInt("categoryId"));

        mockMvc.perform(MockMvcRequestBuilders.get(getUrl)
                .header("Authorization", authorizationHeader))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.categoryId").exists())
                .andExpect(jsonPath("$.categoryId").isNotEmpty())
                .andExpect(jsonPath("$.categoryId").isNumber())
                .andExpect(jsonPath("$.name", is("test-category")))
                .andExpect(jsonPath("$.description", is("This is a test category for test purposes only.")))
                .andExpect(jsonPath("$.organizationId", is(organization1.getOrganizationId())));
    }

    @Test
    public void testCreateCategoryByCoOrganizator() throws Exception {
        User coOrganizator = new User("coOrganizator", "pass");
        coOrganizator.setEmail("coOrganizator@localhost");
        userService.addUser(coOrganizator);

        organization1.addOrganizer(coOrganizator);
        organizationService.updateOrganization(organization1);

        String coOrganizatorToken = TokenProvider.getToken(mockMvc, clientDetails, coOrganizator.getUsername(), "pass");
        String coOrganizatorHeader = String.format("Bearer %s", coOrganizatorToken);

        CreateCategoryResource categoryResource = new CreateCategoryResource();
        categoryResource.setName("test-category");
        categoryResource.setDescription("This is a test category for test purposes only.");

        JSONObject jsonObject = new JSONObject(categoryResource);

        mockMvc.perform(
                MockMvcRequestBuilders.post(baseApiUrl)
                        .header("Authorization", coOrganizatorHeader)
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("organizationId", String.valueOf(organization1.getOrganizationId())))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void testCreateCategoryByNonOrganizator() throws Exception {
        User nonOrganizationUser = new User("nonorganizator", "pass");
        nonOrganizationUser.setEmail("nonorganizator@localhost");
        userService.addUser(nonOrganizationUser);

        String nonOrganizationToken = TokenProvider.getToken(mockMvc, clientDetails, nonOrganizationUser.getUsername(), "pass");
        String nonOrganizationHeader = String.format("Bearer %s", nonOrganizationToken);

        CreateCategoryResource categoryResource = new CreateCategoryResource();
        categoryResource.setName("test-category");
        categoryResource.setDescription("This is a test category for test purposes only.");

        JSONObject jsonObject = new JSONObject(categoryResource);

        mockMvc.perform(
                MockMvcRequestBuilders.post(baseApiUrl)
                        .header("Authorization", nonOrganizationHeader)
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("organizationId", String.valueOf(organization1.getOrganizationId())))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testGetAllCategoriesFromOneOrganization() throws Exception {
        List<CreateCategoryResource> categoryResources = new ArrayList<>();

        CreateCategoryResource categoryResource1 = new CreateCategoryResource();
        categoryResource1.setName("test-category-1");
        categoryResource1.setDescription("This is a test category for test purposes only. [1]");

        categoryResources.add(categoryResource1);

        CreateCategoryResource categoryResource2 = new CreateCategoryResource();
        categoryResource2.setName("test-category-2");
        categoryResource2.setDescription("This is a test category for test purposes only. [2]");

        categoryResources.add(categoryResource2);

        for (CreateCategoryResource categoryResource : categoryResources) {
            JSONObject jsonObject = new JSONObject(categoryResource);

            String response = mockMvc.perform(
                    MockMvcRequestBuilders.post(baseApiUrl)
                            .header("Authorization", authorizationHeader)
                            .content(jsonObject.toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("organizationId", String.valueOf(organization1.getOrganizationId())))
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andReturn().getResponse().getContentAsString();

            JSONObject jsonResponse = new JSONObject(response);

            Assert.assertTrue(jsonResponse.getInt("categoryId") > 0);
        }


        CreateCategoryResource otherOrganisationCategoryResource = new CreateCategoryResource();
        otherOrganisationCategoryResource.setName("test-category-other-organisation");
        otherOrganisationCategoryResource.setDescription("This is a test category for test purposes only. [other-organisation]");


        JSONObject otherOrganizationJsonObject = new JSONObject(otherOrganisationCategoryResource);

        String response = mockMvc.perform(
                MockMvcRequestBuilders.post(baseApiUrl)
                        .header("Authorization", authorizationHeader)
                        .content(otherOrganizationJsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("organizationId", String.valueOf(organization2.getOrganizationId())))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JSONObject jsonResponse3 = new JSONObject(response);

        Assert.assertTrue(jsonResponse3.getInt("categoryId") > 0);


        mockMvc.perform(MockMvcRequestBuilders.get(baseApiUrl)
                .header("Authorization", authorizationHeader)
                .param("organizationId", String.valueOf(organization1.getOrganizationId())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$", hasSize(categoryResources.size())))
                .andExpect(jsonPath("$[0].name", is(categoryResource1.getName())))
                .andExpect(jsonPath("$[0].description", is(categoryResource1.getDescription())))
                .andExpect(jsonPath("$[1].name", is(categoryResource2.getName())))
                .andExpect(jsonPath("$[1].description", is(categoryResource2.getDescription())));
    }
}
