package be.kdg.kandoe.backend.model.organizations;

import be.kdg.kandoe.backend.model.cards.CardDetails;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private int categoryId;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @ManyToOne
    private Organization organization;
    
    @OneToMany
    private List<Tag> tags;
    
    @OneToMany
    private List<Topic> topics;
    
    /*@OneToMany
    private List<CardDetails> cardDetailsList;*/
}
