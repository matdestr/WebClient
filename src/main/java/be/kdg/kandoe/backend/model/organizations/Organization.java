/**
 * An organization consists of a name and has an owner and multiple organizers
 * The organizers have administration rights in the organization
 */
package be.kdg.kandoe.backend.model.organizations;

import be.kdg.kandoe.backend.model.users.User;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
public class Organization {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private int organizationId;
    
    @Column(unique = true, nullable = false)
    private String name;
    
    @OneToOne(fetch = FetchType.EAGER)
    private User owner;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "OrganizationOrganizers")
    private List<User> organizers = new ArrayList<>();

    public Organization(String name, User owner) {
        this.name = name;
        this.owner = owner;
    }


    public void addOrganizer(User user) {
        this.organizers.add(user);
    }
    
    public boolean isOrganizer(User user) {
        return owner.getUserId() == user.getUserId() || organizers.stream().anyMatch(u -> u.getUserId() == user.getUserId());
    }
}
