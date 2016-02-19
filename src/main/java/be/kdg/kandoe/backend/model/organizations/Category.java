package be.kdg.kandoe.backend.model.organizations;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Vincent on 7/02/2016.
 */
@Entity
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue
    @Getter
    private int categoryId;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String description;
    @ManyToOne(cascade=CascadeType.ALL)
    @Getter
    @Setter
    private Organization organization;
    @OneToMany
    @Getter
    @Setter
    private List<Tag> tags;
    @OneToMany
    @Getter
    @Setter
    private List<Topic> topics;
}
