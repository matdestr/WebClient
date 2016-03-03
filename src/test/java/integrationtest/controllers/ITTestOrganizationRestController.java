package integrationtest.controllers;

import be.kdg.kandoe.backend.model.oauth.OAuthClientDetails;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.OAuthClientDetailsService;
import be.kdg.kandoe.backend.service.api.UserService;
import be.kdg.kandoe.frontend.config.RootContextConfig;
import be.kdg.kandoe.frontend.config.WebContextConfig;
import be.kdg.kandoe.frontend.controller.resources.organizations.OrganizationResource;
import integrationtest.TokenProvider;
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

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootContextConfig.class, WebContextConfig.class})
@WebAppConfiguration
@Transactional
public class ITTestOrganizationRestController {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private UserService userService;

    @Autowired
    private OAuthClientDetailsService oAuthClientDetailsService;

    private OAuthClientDetails clientDetails;
    private MockMvc mockMvc;

    private String unencryptedPassword;
    private User user;

    private String token;
    private String authorizationHeader;

    @Before
    public void setup() throws Exception {
        unencryptedPassword = "ownerpasswd";

        User user = new User("owner", unencryptedPassword);
        this.user = userService.addUser(user);

        OAuthClientDetails newClientDetails = new OAuthClientDetails("test");
        newClientDetails.setAuthorizedGrandTypes("password", "refresh_token");
        newClientDetails.setAuthorities("ROLE_TEST_CLIENT");
        newClientDetails.setScopes("read");
        newClientDetails.setSecret("secret");

        this.clientDetails = oAuthClientDetailsService.addClientsDetails(newClientDetails);

        //MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .addFilter(springSecurityFilterChain)
                .build();

        this.token = TokenProvider.getToken(mockMvc, clientDetails, user.getUsername(), unencryptedPassword);
        authorizationHeader = String.format("Bearer %s", token);
    }

    @Test
    public void testCreateNewOrganizationAndGetCreatedOrganization() throws Exception {
        /*String token = TokenProvider.getToken(mockMvc, clientDetails, user.getUsername(), unencryptedPassword);
        String authorizationHeader = String.format("Bearer %s", token);*/

        OrganizationResource organizationResource = new OrganizationResource();
        organizationResource.setName("Karel de Grote");

        JSONObject jsonObject = new JSONObject(organizationResource);

        String response = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/organizations")
                        .header("Authorization", authorizationHeader)
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated())
                //.andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Karel de Grote")))
                .andReturn().getResponse().getContentAsString();

        JSONObject jsonResponse = new JSONObject(response);

        Assert.assertTrue(jsonResponse.getInt("organizationId") > 0);

        String getUrl = String.format("/api/organizations/%d", jsonResponse.getInt("organizationId"));

        mockMvc.perform(
                MockMvcRequestBuilders.get(getUrl)
                        .header("Authorization", authorizationHeader)
        ).andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.organizationId").exists())
                .andExpect(jsonPath("$.organizationId").isNotEmpty())
                .andExpect(jsonPath("$.organizationId").isNumber())
                .andExpect(jsonPath("$.name", is("Karel de Grote")))
                .andExpect(jsonPath("$.owner").exists())
                .andExpect(jsonPath("$.owner.userId").value(this.user.getUserId()));

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/organizations/user/" + user.getUsername())
                        .param("owner", String.valueOf(true))
        ).andExpect(status().isOk())
                //.andExpect(jsonPath("$.organizations").exists())
                //.andExpect(jsonPath("$.organizations[0].name", is("Karel de Grote")))
                //.andExpect(jsonPath("$.organizations[0].owner.username", is(user.getUsername())));
                .andExpect(jsonPath("$[0].name", is("Karel de Grote")))
                .andExpect(jsonPath("$[0].owner.username", is(user.getUsername())))
                .andExpect(jsonPath("$[1]").doesNotExist());
    }

    @Test
    public void testCreateOrganizationWithEmptyName() throws Exception {
        OrganizationResource organizationResource = new OrganizationResource();
        organizationResource.setName("");

        JSONObject jsonObject = new JSONObject(organizationResource);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/organizations")
                        .header("Authorization", authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonObject.toString())
        ).andExpect(status().isUnprocessableEntity());
    }
    
    @Test
    public void testGetOrganizationsOfNonExistingUser() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/organizations/user/" + "notexistinguser")
        ).andExpect(status().isNotFound());
    }

    @Test
    public void testGetOrganizationsOfUser() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/organizations/user/" + user.getUsername())
        )/*.andDo(print())*/.andExpect(status().isOk());
    }
}
