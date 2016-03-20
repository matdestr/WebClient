package be.kdg.kandoe.backend.model.organizations;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * A tag can belong to multiple categories
 * Consists of a name and a description
 */
@Entity
public class Tag {
    @Id
    @GeneratedValue
    @Getter
    private int tagId;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String description;
}
