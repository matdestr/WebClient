package be.kdg.kandoe.backend.model.cards;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class CardDetails {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private int cardDetailsId;
    private String imageUrl;
    @Column(nullable = false)
    private String text;
    @OneToMany
    private List<Comment> comments;
}
