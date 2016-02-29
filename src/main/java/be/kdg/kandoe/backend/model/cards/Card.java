package be.kdg.kandoe.backend.model.cards;

import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Topic;
import be.kdg.kandoe.backend.model.users.User;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Card {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private int cardId;
    /*
    @OneToMany
    private CardPosition cardPosition;
    */
    @OneToOne(optional = false, cascade = CascadeType.ALL)
    private CardDetails cardDetails;
    @OneToOne(optional = false)
    private Category category;
    @OneToMany
    private List<Topic> topics;
    @OneToOne(optional = false)
    private User user;
}
