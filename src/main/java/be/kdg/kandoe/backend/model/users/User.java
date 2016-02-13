package be.kdg.kandoe.backend.model.users;

import be.kdg.kandoe.backend.model.users.roles.Role;
import be.kdg.kandoe.backend.model.users.roles.RoleType;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "`User`")
@NoArgsConstructor
public class User implements Serializable, UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private int userId;

    @Column(unique = true)
    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    private String password;
    @Column(unique = true)
    @Getter
    @Setter
    private String email;
    @Getter
    @Setter
    private String profilePictureUrl;

    @Setter
    private boolean accountNonExpired = true;
    @Setter
    private boolean accountNonLocked = true;
    @Setter
    private boolean enabled = true;
    @Setter
    private boolean credentialsNonExpired = true;

    @OneToMany(targetEntity = Role.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user")
    @Setter
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
}
