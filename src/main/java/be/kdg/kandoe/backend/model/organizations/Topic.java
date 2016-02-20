package be.kdg.kandoe.backend.model.organizations;

import be.kdg.kandoe.backend.model.cards.Card;
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
public class Topic {
    @Id
    @GeneratedValue
    @Getter
    private int topicId;

    @Getter
    @Setter
    private String name;

    @Setter
    @Getter
    private String description;

    @ManyToOne(cascade=CascadeType.ALL)
    @Getter
    @Setter
    private Category category;

    @OneToMany
    @Getter
    @Setter
    private List<Card> cards;
}
