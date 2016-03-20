package be.kdg.kandoe.frontend.controller.rest;

import be.kdg.kandoe.backend.model.organizations.Tag;
import be.kdg.kandoe.backend.service.api.TagService;
import be.kdg.kandoe.frontend.controller.resources.organizations.categories.TagResource;
import be.kdg.kandoe.frontend.controller.resources.organizations.topic.TopicResource;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
/**
 * This controller is responsible for all the functionality of Tag.
 */
@RestController
@RequestMapping("/api/tags")
@PreAuthorize("isAuthenticated()")
public class TagsRestController {
    private final TagService tagService;

    private MapperFacade mapper;

    @Autowired
    public TagsRestController(MapperFacade mapper, TagService tagService) {
        this.tagService = tagService;
        this.mapper = mapper;
    }

    /**
     * Returns all the tags.
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<TagResource>> getTags() {
        List<Tag> tags = tagService.getTags();
        return new ResponseEntity<>(mapper.mapAsList(tags, TagResource.class), HttpStatus.OK);
    }
}
