package be.kdg.kandoe.frontend.config.security.interceptors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JwtHandshakeInterceptor implements HandshakeInterceptor {
    private Logger logger = LoggerFactory.getLogger(JwtHandshakeInterceptor.class);
    private JwtAccessTokenConverter accessTokenConverter;

    public JwtHandshakeInterceptor(JwtAccessTokenConverter accessTokenConverter) {
        this.accessTokenConverter = accessTokenConverter;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> map) throws Exception {
        if (serverHttpRequest instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) serverHttpRequest;
            String token = servletRequest.getServletRequest().getParameter("token");
            if (token == null || token.trim().isEmpty()) return false;

            String verifierKey = accessTokenConverter.getKey().get("value");
            SignatureVerifier verifier;
            if (accessTokenConverter.isPublic()){
                verifier = new RsaVerifier(verifierKey);
            } else {
                verifier = new MacSigner(verifierKey);
            }
            try {
                Jwt jwt = JwtHelper.decodeAndVerify(token, verifier);
                JSONObject claims = new JSONObject(jwt.getClaims());
                HashMap params = new HashMap();
                params.put("user_name", claims.getString("user_name"));

                JSONArray scopesJsonArray = claims.getJSONArray("scope");
                List<String> scopes = new ArrayList<>();
                scopesJsonArray.forEach(s -> scopes.add(s.toString()));
                params.put("scope", scopes);

                params.put("exp", claims.getLong("exp"));

                JSONArray authoritiesJsonArray = claims.getJSONArray("authorities");
                List<String> authorities = new ArrayList<>();
                authoritiesJsonArray.forEach(s -> authorities.add(s.toString()));

                params.put("authorities", authorities);
                params.put("jti", claims.getString("jti"));
                params.put("client_id", claims.getString("client_id"));

                OAuth2AccessToken accessToken = accessTokenConverter.extractAccessToken(token, params);
                if (accessToken.isExpired()) {
                    System.out.println("token is expired");
                    logger.warn("token is expired");
                    return false;
                }
                Authentication authentication = accessTokenConverter.extractAuthentication(params);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e){
                e.printStackTrace();
                logger.warn("Token couldn't be decoded and/or verified");
                return false;
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {

    }
}
