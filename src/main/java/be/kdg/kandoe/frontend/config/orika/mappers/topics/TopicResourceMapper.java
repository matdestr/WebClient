package be.kdg.kandoe.frontend.config.orika.mappers.topics;

import be.kdg.kandoe.backend.model.organizations.Topic;
import be.kdg.kandoe.frontend.controller.resources.organizations.topic.TopicResource;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.springframework.stereotype.Component;

/**
 * Orika mapper for Topic and TopicResource
 */

@Component
public class TopicResourceMapper extends CustomMapper<Topic, TopicResource>{
    @Override
    public void mapAtoB(Topic topic, TopicResource topicResource, MappingContext context) {
        topicResource.setCategoryId(topic.getCategory().getCategoryId());
    }

    @Override
    public void mapBtoA(TopicResource topicResource, Topic topic, MappingContext context) {
        super.mapBtoA(topicResource, topic, context);
    }
}
