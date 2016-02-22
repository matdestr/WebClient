package be.kdg.kandoe.frontend.controller.resources.organizations.topic;

import be.kdg.kandoe.backend.model.organizations.Category;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by thaneestevens on 22/02/16.
 */
@Data
public class CreateTopicResource {
    @NotEmpty
    private String name;
    private String description;
}
