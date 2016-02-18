package integrationtest.controllers;

import be.kdg.kandoe.backend.model.oauth.OAuthClientDetails;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.OAuthClientDetailsService;
import be.kdg.kandoe.backend.service.api.UserService;
import be.kdg.kandoe.frontend.config.RootContextConfig;
import be.kdg.kandoe.frontend.config.WebContextConfig;
import be.kdg.kandoe.frontend.controller.resources.users.CreateUserResource;
import be.kdg.kandoe.frontend.controller.resources.users.UpdateUserResource;
import integrationtest.TokenProvider;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootContextConfig.class, WebContextConfig.class})
@WebAppConfiguration
@Transactional
public class ITTestUserController {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserService userService;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private OAuthClientDetailsService oAuthClientDetailsService;
    private OAuthClientDetails clientDetails;

    private User user;
    private String unencryptedPassword = "password";
    private MockMvc mockMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(springSecurityFilterChain).build();

        OAuthClientDetails newClientDetails = new OAuthClientDetails("test");
        newClientDetails.setAuthorizedGrandTypes("password", "refresh_token");
        newClientDetails.setAuthorities("ROLE_TEST_CLIENT");
        newClientDetails.setScopes("read");
        newClientDetails.setSecret("secret");
        this.clientDetails = oAuthClientDetailsService.addClientsDetails(newClientDetails);

        User user = new User("username", unencryptedPassword);
        this.user = userService.addUser(user);
    }

    @Test
    public void testCreateUser() throws Exception {
        CreateUserResource userResource = new CreateUserResource("test", "pass", "pass", "test@email.com");
        JSONObject jsonObject = new JSONObject(userResource);
        mockMvc.perform(
                post("/api/users/")
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.username", is("test")))
                .andExpect(jsonPath("$.email", is("test@email.com")));
    }

    @Test
    public void testCreateUserWithInvalidEmail() throws Exception {
        CreateUserResource userResource = new CreateUserResource("test", "pass", "pass", "notanemail");
        JSONObject jsonObject = new JSONObject(userResource);
        mockMvc.perform(
                post("/api/users/")
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field", is("email")))
                .andExpect(jsonPath("$.fieldErrors[0].message", is(notNullValue())));
    }

    @Test
    public void testCreateUserWithWrongVerifyPassword() throws Exception {
        CreateUserResource userResource = new CreateUserResource("test", "pass", "pass2", "test@email.com");
        JSONObject jsonObject = new JSONObject(userResource);
        mockMvc.perform(
                post("/api/users/")
                        .content(jsonObject.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field", is("valid")))
                .andExpect(jsonPath("$.fieldErrors[0].message", is(notNullValue())));
    }

    @Test
    public void testFileUpload() throws Exception {
        ClassLoader loader = getClass().getClassLoader();
        MockMultipartFile file = new MockMultipartFile("file", loader.getResourceAsStream("profile.jpg"));
        mockMvc.perform(
                fileUpload(String.format("/api/users/%s/photo", user.getUserId()))
                        .file(file)
                        .header("Authorization", String.format("Bearer %s", TokenProvider.getToken(mockMvc, clientDetails, user.getUsername(), unencryptedPassword)))
        )/*.andDo(print())*/.andExpect(status().isOk());
    }

    @Test
    public void testEmptyFileUpload() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", new byte[0]);
        mockMvc.perform(
                fileUpload(String.format("/api/users/%s/photo", user.getUserId()))
                        .file(file)
                        .header("Authorization", String.format("Bearer %s", TokenProvider.getToken(mockMvc, clientDetails, user.getUsername(), unencryptedPassword)))
        )/*.andDo(print())*/.andExpect(status().isBadRequest());
    }

    @Test
    public void testFileUploadWithWrongUserId() throws Exception {
        ClassLoader loader = getClass().getClassLoader();
        MockMultipartFile file = new MockMultipartFile("file", loader.getResourceAsStream("profile.jpg"));
        mockMvc.perform(
                fileUpload(String.format("/api/users/%s/photo", user.getUserId() + 1))
                        .file(file)
                        .header("Authorization", String.format("Bearer %s", TokenProvider.getToken(mockMvc, clientDetails, user.getUsername(), unencryptedPassword)))
        )/*.andDo(print())*/.andExpect(status().isUnauthorized());
    }

    @Test
    public void testChangeProfile() throws Exception {
        UpdateUserResource userResource = new UpdateUserResource();
        userResource.setUsername("new username");
        userResource.setEmail("newemail@email.com");
        userResource.setName("newname");
        userResource.setSurname("newsurname");
        userResource.setVerifyPassword(unencryptedPassword);

        mockMvc.perform(put(String.format("/api/users/%s/", user.getUserId()))
                .header("Authorization", String.format("Bearer %s", TokenProvider.getToken(mockMvc, clientDetails, user.getUsername(), unencryptedPassword)))
                .content(new JSONObject(userResource).toString())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("new username")))
                .andExpect(jsonPath("$.name", is("newname")))
                .andExpect(jsonPath("$.surname", is("newsurname")));
    }

    @Test
    public void testGetExistingUser() throws Exception {
        mockMvc.perform(
                get(String.format("/api/users/%s", user.getUserId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.surname", is(user.getSurname())));
    }

    @Test
    public void testGetNotExistingUser() throws Exception {
        mockMvc.perform(
                get("/api/users/notExistingUser")).andExpect(status().isNotFound());
    }
}
