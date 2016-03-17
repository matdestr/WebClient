package be.kdg.kandoe.backend.persistence.api;

import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Proxy interface for the CRUD Repository for the {@link Topic} model
 */

public interface TopicRepository extends JpaRepository<Topic,Integer> {
    Topic findTopicByNameAndCategory(String name, Category category);

    Topic findTopicByTopicId(int topicId);

    List<Topic> findTopicByCategoryCategoryId(int categoryId);
}
