package be.kdg.kandoe.backend.model.cards;

import be.kdg.kandoe.backend.model.organizations.Topic;
import be.kdg.kandoe.backend.model.users.User;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class CardDetails {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private int cardDetailsId;

    @Column(nullable = false)
    @Length(min = 3)
    private String text;
    
    @URL(regexp = ".*\\.(jpg|png)$")
    private String imageUrl;

    @ManyToOne(optional = false)
    private User creator;
    
    @ManyToMany
    @Size(min = 1)
    private Set<Topic> topics;
    
    @OneToMany
    private List<Comment> comments;
}
