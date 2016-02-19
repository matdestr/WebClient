package be.kdg.kandoe.backend.model.cards;

import be.kdg.kandoe.backend.model.users.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Card {
    @Id
    @GeneratedValue
    @Getter
    private int cardId;
    @Getter
    @Setter
    private int priority;
    @OneToOne
    @Getter
    @Setter
    private CardDetails cardDetails;
    @OneToOne
    @Getter
    @Setter
    private User user;
}
