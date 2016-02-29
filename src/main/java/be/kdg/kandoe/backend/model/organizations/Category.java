package be.kdg.kandoe.backend.model.organizations;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@Data
public class Category {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private int categoryId;
    @Column(nullable = false)
    private String name;
    private String description;
    @ManyToOne
    private Organization organization;
    @OneToMany
    private List<Tag> tags;
    @OneToMany
    private List<Topic> topics;

}
