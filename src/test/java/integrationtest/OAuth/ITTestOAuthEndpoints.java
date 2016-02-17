package integrationtest.OAuth;

import be.kdg.kandoe.backend.model.oauth.OAuthClientDetails;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.model.users.roles.RoleType;
import be.kdg.kandoe.backend.service.api.OAuthClientDetailsService;
import be.kdg.kandoe.backend.service.api.UserService;
import be.kdg.kandoe.frontend.config.RootContextConfig;
import be.kdg.kandoe.frontend.config.WebContextConfig;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {RootContextConfig.class, WebContextConfig.class, OAuth2TestConfig.class})
@Transactional
public class ITTestOAuthEndpoints {
    @Autowired
    WebApplicationContext context;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private OAuthClientDetailsService oAuthClientDetailsService;
    private OAuthClientDetails clientDetails;

    @Autowired
    private UserService userService;
    
    private String unencryptedPassword = "password";
    private User user;

    private MockMvc mvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.webAppContextSetup(context).addFilter(springSecurityFilterChain).build();

        OAuthClientDetails newClientDetails = new OAuthClientDetails("test");
        newClientDetails.setAuthorizedGrandTypes("password", "refresh_token");
        newClientDetails.setAuthorities("ROLE_TEST_CLIENT");
        newClientDetails.setScopes("read");
        newClientDetails.setSecret("secret");

        User testUser = new User("oauthtestuser", unencryptedPassword);
        this.user = userService.addUser(testUser);

        this.clientDetails = oAuthClientDetailsService.addClientsDetails(newClientDetails);
    }

    @Test
    public void testNotProtectedMethod() throws Exception {
        mvc.perform(get("/test")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("test"));
    }

    @Test
    public void testProtectedMethodAsUnauthorized() throws Exception {
        mvc.perform(get("/test/auth")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    private String getToken() throws Exception {
        String base64authorizationString = new String(Base64Utils.encode(String.format("%s:%s", clientDetails.getClientId(), clientDetails.getClientSecret()).getBytes()));
        String authorizationHeader = String.format("Basic %s", base64authorizationString);
        
        String token = mvc.perform(
                post("/oauth/token")
                        .header("Authorization", authorizationHeader)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("grant_type", "password")
                        .param("client_id", clientDetails.getClientId())
                        .param("client_secret", clientDetails.getClientSecret())
                        .param("username", user.getUsername())
                        .param("password", unencryptedPassword)
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn().getResponse().getContentAsString();

        JSONObject jsonObject = new JSONObject(token);

        return jsonObject.getString("access_token");
    }

    @Test
    public void testTokenEndpoint() throws Exception{
        getToken();
    }


    @Test
    public void testProtectedMethodAsAuthorized() throws Exception{
        String token = getToken();
        String authorizationHeader = String.format("Bearer %s", token);
        mvc.perform(get("/test/auth")
                .header("Authorization", authorizationHeader)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("test"));
    }

    @Test
    public void testProtectedMethodWithAdminRoleAsNonAdminUser() throws Exception {
        String token = getToken();
        String authorizationHeader = String.format("Bearer %s", token);
        mvc.perform(get("/test/admin")
                .header("Authorization", authorizationHeader)
                .accept(MediaType.APPLICATION_JSON))
                //.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void testProtectedMethodWithAdminRoleAsAdmin() throws Exception {
        user.addRole(RoleType.ROLE_ADMIN);
        userService.updateUser(user);

        String token = getToken();
        String authorizationHeader = String.format("Bearer %s", token);

        mvc.perform(get("/test/admin")
                .header("Authorization", authorizationHeader)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
