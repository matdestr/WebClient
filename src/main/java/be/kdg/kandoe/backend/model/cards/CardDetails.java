/**
 * CardDetails consist of a text, image, creator and a list of comments
 * A CardDetail can belong to one category and multiple topics
 */

package be.kdg.kandoe.backend.model.cards;

import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Topic;
import be.kdg.kandoe.backend.model.users.User;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashSet;
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

    @URL(regexp = ".*\\.(jpg|jpeg|png)$")
    private String imageUrl;

    @ManyToOne(optional = false)
    private User creator;
    
    @ManyToOne(optional = false, cascade = CascadeType.REFRESH)
    private Category category;

    @ManyToMany(fetch = FetchType.EAGER)
    @Size(min = 0)
    private Set<Topic> topics = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Fetch(FetchMode.SELECT) // Hibernate workaround: fixes duplicate entities when retrieved
    private List<Comment> comments = new ArrayList<>();
}
