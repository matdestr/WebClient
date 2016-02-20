package be.kdg.kandoe.backend.persistence.api;

import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by thaneestevens on 20/02/16.
 */
public interface TopicRepository extends JpaRepository<Topic,Integer> {
    Topic findTopicByNameAndCategory(String name, Category category);

}
