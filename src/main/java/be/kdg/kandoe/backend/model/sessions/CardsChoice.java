package be.kdg.kandoe.backend.model.sessions;

import be.kdg.kandoe.backend.model.cards.CardDetails;
import be.kdg.kandoe.backend.model.users.User;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.List;

@Data
@Entity
public class CardsChoice {
    @Id
    private User participant;
    
    @ManyToMany
    private List<CardDetails> chosenCards;
}
