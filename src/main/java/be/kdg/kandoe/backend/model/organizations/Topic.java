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

    @OneToMany
    @Getter
    @Setter
    private List<Card> cards;
}
