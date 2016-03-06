package be.kdg.kandoe.backend.model.organizations;

import be.kdg.kandoe.backend.model.users.User;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Data
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
    @JoinTable(name = "OrganizationMembers")
    private List<User> members;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "OrganizationOrganizers")
    private List<User> organizers;

    public Organization(String name, User owner) {
        this.name = name;
        this.owner = owner;
        this.members = new ArrayList<>();
        this.organizers = new ArrayList<>();
    }

    public void addMember(User user) {
        this.members.add(user);
    }
    
    public void addOrganizer(User user) {
        this.organizers.add(user);
    }
    
    public boolean isOrganizer(User user) {
        return (this.owner.equals(user) || this.organizers.contains(user));
    }
}
