package be.kdg.kandoe.backend.service.api;

import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Topic;
import be.kdg.kandoe.backend.service.exceptions.TopicServiceException;

import java.util.List;

/**
 * Interface for managing the {@link Topic}s of of categories.
 */
public interface TopicService {
    /**
     * Adds a new topic.
     * */
    Topic addTopic(Topic topic) throws TopicServiceException;
    
    /**
     * Retrieves a topic by its given unique ID.
     * */
    Topic getTopicByTopicId(int topicId);
    
    /**
     * Retrieves a topic by its given name and category it belongs to.
     * 
     * @param name The name of the topic
     * @param category The category the topic belongs to
     * */
    Topic getTopicByName(String name, Category category);
    
    /**
     * Retrieves the topics of a category by the given unique category ID.
     * 
     * @param categoryId The unique ID of the category
     * */
    List<Topic> getTopicsByCategoryId(int categoryId);
    
    /**
     * Updates the state of a topic.
     * 
     * @param topic The {@link Topic} to update the state of
     * */
    void updateTopic(Topic topic) throws TopicServiceException;
}
