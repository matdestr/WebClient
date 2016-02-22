package be.kdg.kandoe.frontend.controller.rest;

import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.organizations.Topic;
import be.kdg.kandoe.backend.service.api.CategoryService;
import be.kdg.kandoe.backend.service.api.OrganizationService;
import be.kdg.kandoe.backend.service.api.TopicService;
import be.kdg.kandoe.frontend.controller.resources.organizations.categories.CategoryResource;
import be.kdg.kandoe.frontend.controller.resources.organizations.categories.CreateCategoryResource;
import be.kdg.kandoe.frontend.controller.resources.organizations.topic.CreateTopicResource;
import be.kdg.kandoe.frontend.controller.resources.organizations.topic.TopicResource;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by thaneestevens on 22/02/16.
 */
@RestController
@RequestMapping("/api/topics")
public class TopicRestController
{

    private final TopicService topicService;
    private final CategoryService categoryService;

    private MapperFacade mapper;

    @Autowired
    public TopicRestController(MapperFacade mapper,TopicService topicService,
                                  CategoryService categoryService
                                  ) {
        this.mapper = mapper;
        this.topicService = topicService;
        this.categoryService = categoryService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TopicResource> createTopic(@RequestParam("categoryId") int categoryId,
                                                        @Valid @RequestBody CreateTopicResource topicResource) {
        Category category = categoryService.getCategoryById(categoryId);
        Topic topic = mapper.map(topicResource, Topic.class);
        topic.setCategory(category);
        topic = topicService.addTopic(topic);

        return new ResponseEntity<>(mapper.map(topic, TopicResource.class), HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TopicResource>> getTopics(@RequestParam("categoryId") int categoryId) {
        List<Topic> topics = topicService.getTopicsByCategoryId(categoryId);

        return new ResponseEntity<>(mapper.mapAsList(topics, TopicResource.class), HttpStatus.OK);
    }

    @RequestMapping(value = "/{topicId}", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TopicResource> getTopic(@PathVariable("topicId") int topicId) {
        Topic topic = topicService.getTopicByTopicId(topicId);

        return new ResponseEntity<>(mapper.map(topic, TopicResource.class), HttpStatus.OK);
    }
}
