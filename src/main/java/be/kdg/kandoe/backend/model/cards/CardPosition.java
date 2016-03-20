package be.kdg.kandoe.backend.model.cards;

import be.kdg.kandoe.backend.model.sessions.Session;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;

/**
 * Defines the position of a card on the circle
 */
@Entity
@Data
@NoArgsConstructor
public class CardPosition {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private int cardPositionId;

    @OneToOne
    @Setter(AccessLevel.NONE)
    private CardDetails cardDetails;

    @OneToOne
    @Setter(AccessLevel.NONE)
    private Session session;

    @Min(1)
    private int priority;

    public CardPosition(CardDetails cardDetails, Session session) {
        this.cardDetails = cardDetails;
        this.session = session;
        this.priority = 1;
    }
}
