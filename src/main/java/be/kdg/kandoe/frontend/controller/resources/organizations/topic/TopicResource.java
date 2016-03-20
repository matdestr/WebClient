package be.kdg.kandoe.frontend.controller.resources.organizations.topic;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Resource for a topic
 */

@Data
@NoArgsConstructor
public class TopicResource {
    private int topicId;
    private String name;
    private String description;
    private int categoryId;
}
