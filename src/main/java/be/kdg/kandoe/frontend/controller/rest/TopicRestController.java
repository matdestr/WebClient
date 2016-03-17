package be.kdg.kandoe.frontend.controller.rest;

import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.organizations.Topic;
import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.backend.model.sessions.SynchronousSession;
import be.kdg.kandoe.backend.service.api.CategoryService;
import be.kdg.kandoe.backend.service.api.OrganizationService;
import be.kdg.kandoe.backend.service.api.SessionService;
import be.kdg.kandoe.backend.service.api.TopicService;
import be.kdg.kandoe.frontend.controller.resources.organizations.categories.CategoryResource;
import be.kdg.kandoe.frontend.controller.resources.organizations.categories.CreateCategoryResource;
import be.kdg.kandoe.frontend.controller.resources.organizations.topic.CreateTopicResource;
import be.kdg.kandoe.frontend.controller.resources.organizations.topic.TopicResource;
import be.kdg.kandoe.frontend.controller.resources.sessions.AsynchronousSessionResource;
import be.kdg.kandoe.frontend.controller.resources.sessions.SessionResource;
import be.kdg.kandoe.frontend.controller.resources.sessions.SynchronousSessionResource;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/topics")
public class TopicRestController
{

    private final TopicService topicService;
    private final CategoryService categoryService;
    private final SessionService sessionService;

    private MapperFacade mapper;

    @Autowired
    public TopicRestController(MapperFacade mapper,TopicService topicService,
                                  CategoryService categoryService, SessionService sessionService
                                  ) {
        this.mapper = mapper;
        this.topicService = topicService;
        this.categoryService = categoryService;
        this.sessionService = sessionService;
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

    @RequestMapping(value = "/{topicId}/sessions", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SessionResource>> getSessionsFromTopic(@PathVariable("topicId") int topicId) {

        List<Session> sessions = sessionService.getSessionsFromTopic(topicId);

        List<SessionResource> sessionResources = new ArrayList<>();

        // Workaround for NullPointerException due to casting issues with mapper
        for (Session session : sessions) {
            SessionResource resource;

            if (session instanceof SynchronousSession) {
                resource = mapper.map(session, SynchronousSessionResource.class);
            } else {
                resource = mapper.map(session, AsynchronousSessionResource.class);
            }

            sessionResources.add(resource);
        }
        return new ResponseEntity<>(sessionResources, HttpStatus.OK);
        //return new ResponseEntity<>(mapper.mapAsList(sessions, SessionResource.class), HttpStatus.OK);
    }

    @RequestMapping(value = "/{topicId}", method = RequestMethod.PUT)
    public ResponseEntity setOrganizationName(@PathVariable("topicId") int topicId, @RequestParam(value="topicName")String topicName){
        Topic topic = topicService.getTopicByTopicId(topicId);
        topic.setName(topicName);
        topicService.updateTopic(topic);
        TopicResource resource = mapper.map(topic, TopicResource.class);

        return new ResponseEntity<>(resource,HttpStatus.OK);
    }
}
