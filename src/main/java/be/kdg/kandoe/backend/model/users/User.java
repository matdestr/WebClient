package be.kdg.kandoe.backend.model.users;

import be.kdg.kandoe.backend.model.users.roles.Role;
import be.kdg.kandoe.backend.model.users.roles.RoleType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "`User`")
@NoArgsConstructor
@Data
public class User implements Serializable, UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Column(unique = true, nullable = false)
    private String username;
    
    private String password;
    
    @Column(unique = true)
    private String email;
    
    private String profilePictureUrl;
    
    @Column(length = 100)
    private String name;
    
    @Column(length = 100)
    private String surname;

    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean enabled = true;
    private boolean credentialsNonExpired = true;

    @OneToMany(targetEntity = Role.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user")
    private List<Role> roles = new ArrayList<>();

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(r -> r.getAuthorities()).flatMap(a -> a.stream()).collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void addRole(RoleType roleType) {
        roles.add(Role.getRole(roleType));
    }

    public void addRole(RoleType... roleTypes) {
        Arrays.stream(roleTypes).forEach(this::addRole);
    }

    public List<RoleType> getRoleTypes() {
        return roles.stream().map(r -> r.getRoleType()).collect(Collectors.toList());
    }
}
