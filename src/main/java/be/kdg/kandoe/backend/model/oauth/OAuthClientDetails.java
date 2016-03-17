/**
 * Authorization of a user using a token
 */
package be.kdg.kandoe.backend.model.oauth;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

import javax.persistence.*;
import java.util.*;

@Entity
@Data
public class OAuthClientDetails implements ClientDetails {
    @Id
    @GeneratedValue
    private int id;

    @Column(unique = true)
    private String clientId;
    private String clientSecret;

    private int accessTokenValiditySeconds;
    private int refreshTokenValiditySeconds;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> resourceIds;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> scopes;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> autoApprovedScopes;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> authorizedGrantTypes;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String>  registeredRedirectUri;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> authorities;

    private boolean autoApproved;
    private boolean secretRequired;

    public OAuthClientDetails(){
        resourceIds = new HashSet<>();
        scopes = new HashSet<>();
        authorizedGrantTypes = new HashSet<>();
        registeredRedirectUri = new HashSet<>();
        authorities = new ArrayList<String>();

        autoApproved = false;
        secretRequired = false;
    }

    public OAuthClientDetails(String clientId){
        this();
        this.clientId = clientId;
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public Set<String> getResourceIds() {
        return resourceIds;
    }

    @Override
    public boolean isSecretRequired() {
        return secretRequired;
    }

    @Override
    public String getClientSecret() {
        return clientSecret;
    }

    @Override
    public boolean isScoped() {
        return scopes.size() > 0;
    }

    @Override
    public Set<String> getScope() {
        return scopes;
    }

    @Override
    public Set<String> getAuthorizedGrantTypes() {
        return authorizedGrantTypes;
    }

    @Override
    public Set<String> getRegisteredRedirectUri() {
        return registeredRedirectUri;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        authorities.forEach(s -> grantedAuthorities.add(new SimpleGrantedAuthority(s)));
        return grantedAuthorities;
    }

    @Override
    public Integer getAccessTokenValiditySeconds() {
        return accessTokenValiditySeconds;
    }

    @Override
    public Integer getRefreshTokenValiditySeconds() {
        return refreshTokenValiditySeconds;
    }

    public void setAutoApproved(boolean autoApproved){
        this.autoApproved = autoApproved;
    }

    public void setAutoApproved(String... scopes){
        this.autoApprovedScopes = new HashSet<>(Arrays.asList(scopes));
    }

    @Override
    public boolean isAutoApprove(String scope) {
        return autoApproved || autoApprovedScopes.contains(scope);
    }

    @Override
    public Map<String, Object> getAdditionalInformation() {
        return Collections.emptyMap();
    }

    public void setAuthorizedGrandTypes(String... authorizedGrandTypes) {
        this.authorizedGrantTypes = new HashSet<>(Arrays.asList(authorizedGrandTypes));
    }

    public void setAuthorities(String... authorities) {
        this.authorities = new ArrayList(Arrays.asList(authorities));
    }

    public void setScopes(String... scopes) {
        this.scopes = new HashSet<>(Arrays.asList(scopes));
    }

    public void setSecret(String secret) {
        this.clientSecret = secret;
    }
}
