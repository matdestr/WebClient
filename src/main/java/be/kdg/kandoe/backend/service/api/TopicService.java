package be.kdg.kandoe.backend.service.api;

import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Topic;
import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.backend.service.exceptions.TopicServiceException;

import java.util.List;

/**
 * Interface contract for the {@link Topic} model
 */

public interface TopicService {
    Topic addTopic(Topic topic) throws TopicServiceException;
    Topic getTopicByTopicId(int topicId);
    Topic getTopicByName(String name, Category category);
    List<Topic> getTopicsByCategoryId(int categoryId);
    void updateTopic(Topic topic) throws TopicServiceException;
}
