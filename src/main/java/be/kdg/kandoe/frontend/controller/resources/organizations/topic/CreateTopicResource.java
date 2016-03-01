package be.kdg.kandoe.frontend.controller.resources.organizations.topic;

import be.kdg.kandoe.backend.model.organizations.Category;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class CreateTopicResource {
    @NotEmpty(message = "{topic.wrong.name}")
    private String name;
    private String description;
}
