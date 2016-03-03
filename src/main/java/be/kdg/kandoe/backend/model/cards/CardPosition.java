package be.kdg.kandoe.backend.model.cards;

import be.kdg.kandoe.backend.model.sessions.Session;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class CardPosition {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private int cardPositionId;
    private int priority;
    @OneToOne
    private Session session;
}
