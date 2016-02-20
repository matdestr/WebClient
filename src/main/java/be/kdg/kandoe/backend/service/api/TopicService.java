package be.kdg.kandoe.backend.service.api;

import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Topic;
import be.kdg.kandoe.backend.service.exceptions.TopicServiceException;

/**
 * Created by thaneestevens on 20/02/16.
 */
public interface TopicService {
    Topic addTopic(Topic topic) throws TopicServiceException;
    Topic getTopicByName(String name, Category category);
}
