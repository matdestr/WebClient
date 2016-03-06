package be.kdg.kandoe.backend.model.organizations;

import be.kdg.kandoe.backend.model.cards.CardDetails;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@EqualsAndHashCode(exclude = { "organization", "cards" })
@NoArgsConstructor
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
    
    //@OneToMany(targetEntity = CardDetails.class, fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE })
    @OneToMany(targetEntity = CardDetails.class, fetch = FetchType.EAGER, mappedBy = "category"/*, cascade = CascadeType.ALL*/) // mappedBy is needed to prevent unique key violation!
    private List<CardDetails> cards;

    @OneToMany
    private List<Topic> topics;
}
