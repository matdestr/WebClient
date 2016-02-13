package be.kdg.kandoe.backend.model.organizations;

import be.kdg.kandoe.backend.model.users.User;

import javax.persistence.*;
import java.util.List;

@Entity
public class Organization {

    public Organization() {
    }

    public Organization(String name, User owner) {
        this.name = name;
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    @Id
    private int organizationId;
    @Column(unique = true)
    private String name;
    @OneToOne
    private User owner;
    @OneToMany
    private List<User> members;
}
