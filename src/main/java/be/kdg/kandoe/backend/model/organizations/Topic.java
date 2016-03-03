package be.kdg.kandoe.backend.model.organizations;

import be.kdg.kandoe.backend.model.cards.CardDetails;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
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

    @ManyToMany
    private Set<CardDetails> cards;
}
