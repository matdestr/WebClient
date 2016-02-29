package be.kdg.kandoe.backend.service.impl;

import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Topic;
import be.kdg.kandoe.backend.persistence.api.TopicRepository;
import be.kdg.kandoe.backend.service.api.TopicService;
import be.kdg.kandoe.backend.service.exceptions.TopicServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TopicServiceImpl implements TopicService {
    private final TopicRepository topicRepository;

    @Autowired
    public TopicServiceImpl(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    @Override
    public Topic addTopic(Topic topic){
        Topic fetchedTopic = getTopicByName(topic.getName(),topic.getCategory());

        if(fetchedTopic!=null)
              if(fetchedTopic.getCategory().equals(topic.getCategory()))
                  throw new TopicServiceException(String.format(
                          "Topic name '%s' already exists in category '%s'.",
                          fetchedTopic.getName(), fetchedTopic.getCategory().getName()));


        topicRepository.save(topic);

        return topic;
    }

    @Override
    public Topic getTopicByName(String name, Category category) {
        return topicRepository.findTopicByNameAndCategory(name,category);
    }

    @Override
    public List<Topic> getTopicsByCategoryId(int categoryId) {
        return topicRepository.findTopicByCategoryCategoryId(categoryId);
    }

    @Override
    public Topic getTopicByTopicId(int topicId) {
        return topicRepository.findTopicByTopicId(topicId);
    }
}
