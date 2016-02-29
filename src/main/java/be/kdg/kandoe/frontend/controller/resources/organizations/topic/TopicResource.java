package be.kdg.kandoe.frontend.controller.resources.organizations.topic;

import be.kdg.kandoe.frontend.controller.resources.organizations.OrganizationResource;
import be.kdg.kandoe.frontend.controller.resources.organizations.categories.CategoryResource;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;


@Data
@NoArgsConstructor
public class TopicResource {
    private int topicId;
    private String name;
    private String description;
    private int categoryId;
}
