package be.kdg.kandoe.backend.model.organizations;

import be.kdg.kandoe.backend.model.cards.CardDetails;
import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"organization", "tags", "cards", "topics"})
public class Category {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private int categoryId;

    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne(optional = false)
    private Organization organization;

    @OneToMany
    private List<Tag> tags;
    
    //@OneToMany(targetEntity = CardDetails.class, fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE })
    @OneToMany(targetEntity = CardDetails.class, fetch = FetchType.EAGER, mappedBy = "category"/*, cascade = CascadeType.ALL*/) // mappedBy is needed to prevent unique key violation!
    private Set<CardDetails> cards;

    @OneToMany
    private List<Topic> topics;
}
