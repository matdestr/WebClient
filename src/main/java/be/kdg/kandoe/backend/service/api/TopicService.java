package be.kdg.kandoe.backend.service.api;

import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Topic;
import be.kdg.kandoe.backend.service.exceptions.TopicServiceException;

import java.util.List;

public interface TopicService {
    Topic addTopic(Topic topic) throws TopicServiceException;
    
    Topic getTopicByName(String name, Category category);

    List<Topic> getTopicsByCategoryId(int categoryId);

    Topic getTopicByTopicId(int topicId);
}
