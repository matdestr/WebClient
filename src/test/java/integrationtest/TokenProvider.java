package integrationtest;

import be.kdg.kandoe.backend.model.oauth.OAuthClientDetails;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.Base64Utils;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TokenProvider {
    public static String getToken(MockMvc mockMvc, OAuthClientDetails clientDetails, 
                                  String username, String unencryptedPassword) throws Exception {
        String base64authorizationString = new String(Base64Utils.encode(String.format("%s:%s", clientDetails.getClientId(), clientDetails.getClientSecret()).getBytes()));
        String authorizationHeader = String.format("Basic %s", base64authorizationString);

        String token = mockMvc.perform(
                MockMvcRequestBuilders.post("/oauth/token")
                        .header("Authorization", authorizationHeader)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("grant_type", "password")
                        .param("client_id", clientDetails.getClientId())
                        .param("client_secret", clientDetails.getClientSecret())
                        .param("username", username)
                        .param("password", unencryptedPassword)
        ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn().getResponse().getContentAsString();

        JSONObject jsonObject = new JSONObject(token);

        return jsonObject.getString("access_token");
    }
}
