package be.kdg.kandoe.backend.model.cards;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@NoArgsConstructor
public class CardDetails {
    @Id
    @GeneratedValue
    @Getter
    private int cardDetailsId;
    @Getter
    @Setter
    private String imageUrl;
    @Getter
    @Setter
    private String text;
    @OneToMany
    @Getter
    @Setter
    private List<Comment> comments;
}
