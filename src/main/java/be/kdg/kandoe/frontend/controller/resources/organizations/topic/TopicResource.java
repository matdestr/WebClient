package be.kdg.kandoe.frontend.controller.resources.organizations.topic;

import be.kdg.kandoe.frontend.controller.resources.organizations.OrganizationResource;
import be.kdg.kandoe.frontend.controller.resources.organizations.categories.CategoryResource;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by thaneestevens on 22/02/16.
 */
@Data
public class TopicResource {
    private int topicId;
    @NotEmpty
    private String name;
    private String description;
    private CategoryResource category;
}
