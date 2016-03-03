package be.kdg.kandoe.backend.model.cards;

import be.kdg.kandoe.backend.model.users.User;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private int commentId;
    @OneToOne
    private User user;
    private LocalDateTime datePosted;
}
