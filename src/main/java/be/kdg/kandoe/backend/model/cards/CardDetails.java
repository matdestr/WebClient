package be.kdg.kandoe.backend.model.cards;

import be.kdg.kandoe.backend.model.organizations.Category;
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
@EqualsAndHashCode(exclude = {"creator", "category", "topics", "comments"})
@ToString(exclude = {"creator", "category", "topics", "comments"}, doNotUseGetters = true)
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
    
    @ManyToOne(optional = false, cascade = CascadeType.REFRESH)
    private Category category;

    @ManyToMany
    @Size(min = 1)
    private Set<Topic> topics;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Comment> comments;
}
