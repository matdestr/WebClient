package be.kdg.kandoe.backend.model.organizations;

import be.kdg.kandoe.backend.model.cards.CardDetails;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * A Topic belongs to one category
 * Consists of a name and a description
 * Has a list of cards
 */
@Entity
@Data
@EqualsAndHashCode(exclude = {"category", "cards"})
//@NoArgsConstructor
public class Topic {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private int topicId;

    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    private Category category;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<CardDetails> cards;

    public Topic() {
        this.cards = new HashSet<>();
    }
}
