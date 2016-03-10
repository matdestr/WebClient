package be.kdg.kandoe.backend.model.users.roles;

import be.kdg.kandoe.backend.model.users.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;


@Entity
@Table(name = "Role")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "RoleType", discriminatorType = DiscriminatorType.STRING)
@Data
@ToString(exclude = {"user"}, doNotUseGetters = true)
public abstract class Role {
    @Id
    @GeneratedValue
    @Column(nullable = false)
    private Integer roleId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "roles")
    private List<User> user;

    public abstract Collection<? extends GrantedAuthority> getAuthorities();
    public abstract RoleType getRoleType();

    public static Role getRole(RoleType roleType) {
        switch (roleType){
            case ROLE_ADMIN:
                return new Admin();
            case ROLE_CLIENT:
                return new Client();
            default:
                return null;
        }
    }
}
