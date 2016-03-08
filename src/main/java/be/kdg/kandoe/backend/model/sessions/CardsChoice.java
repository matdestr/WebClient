package be.kdg.kandoe.backend.model.sessions;

import be.kdg.kandoe.backend.model.cards.CardDetails;
import be.kdg.kandoe.backend.model.users.User;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@EqualsAndHashCode(exclude = {"participant", "chosenCards", "cardsChosen"})
public class CardsChoice {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private int cardsChoiceId;

    @ManyToOne
    private User participant;

    private boolean cardsChosen;

    @ManyToMany
    private List<CardDetails> chosenCards;
}
