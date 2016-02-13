package be.kdg.kandoe.backend.model.organizations;

import be.kdg.kandoe.backend.model.users.User;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
public class Organization {
    @Id
    @GeneratedValue
    @Getter
    private int organizationId;
    @Column(unique = true)
    @Getter
    @Setter
    private String name;
    @OneToOne
    @Getter
    @Setter
    private User owner;
    @OneToMany
    @Getter
    private List<User> members;

    public Organization(String name, User owner) {
        this.name = name;
        this.owner = owner;
    }
}
