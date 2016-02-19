package be.kdg.kandoe.backend.model.cards;

import be.kdg.kandoe.backend.model.users.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

/**
 * Created by Vincent on 7/02/2016.
 */
@Entity
public class Comment {
    @Id
    @GeneratedValue
    @Getter
    private int commentId;
    @OneToOne
    @Getter
    @Setter
    private User user;
    @Getter
    @Setter
    private LocalDateTime datePosted;
}
