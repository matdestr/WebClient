package integrationtest.controllers;

import be.kdg.kandoe.backend.model.oauth.OAuthClientDetails;
import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.organizations.Tag;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.*;
import be.kdg.kandoe.frontend.config.RootContextConfig;
import be.kdg.kandoe.frontend.config.WebContextConfig;
import integrationtest.TokenProvider;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootContextConfig.class, WebContextConfig.class})
@WebAppConfiguration
@Transactional
public class ITTagsRestController {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private OAuthClientDetailsService oAuthClientDetailsService;

    @Autowired
    private TagService tagService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private CategoryService categoryService;

    private OAuthClientDetails clientDetails;
    private MockMvc mockMvc;

    private String authorizationHeader;

    @Value("/api/tags")
    private String baseApiUrl;

    private Category category1;

    private Organization organization1;


    @Before
    public void setup() throws Exception {
        String unencryptedPassword = "test-password";
        User user = new User("test-user", unencryptedPassword);
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


    }

    @Test
    public void testGetAllTags() throws Exception{
        List<Tag> tagList = tagService.getTags();
        String response = mockMvc.perform(MockMvcRequestBuilders.get(baseApiUrl)
                .header("Authorization", authorizationHeader)
                .param("organizationId", String.valueOf(organization1.getOrganizationId())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$" ,hasSize(tagList.size()) ))
                .andReturn().getResponse().getContentAsString();

        System.out.println(response);
    }

}
