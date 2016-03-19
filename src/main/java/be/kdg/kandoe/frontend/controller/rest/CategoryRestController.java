package be.kdg.kandoe.frontend.controller.rest;

import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.organizations.Tag;
import be.kdg.kandoe.backend.model.sessions.AsynchronousSession;
import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.backend.model.sessions.SynchronousSession;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.CategoryService;
import be.kdg.kandoe.backend.service.api.OrganizationService;
import be.kdg.kandoe.backend.service.api.SessionService;
import be.kdg.kandoe.backend.service.api.TagService;
import be.kdg.kandoe.frontend.controller.resources.organizations.categories.CategoryResource;
import be.kdg.kandoe.frontend.controller.resources.organizations.categories.CreateCategoryResource;
import be.kdg.kandoe.frontend.controller.resources.sessions.AsynchronousSessionResource;
import be.kdg.kandoe.frontend.controller.resources.sessions.SessionResource;
import be.kdg.kandoe.frontend.controller.resources.sessions.SynchronousSessionResource;
import be.kdg.kandoe.frontend.controller.rest.exceptions.CanDoControllerRuntimeException;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryRestController {

    private final CategoryService categoryService;
    private final OrganizationService organizationService;
    private final SessionService sessionService;
    private final TagService tagService;

    private MapperFacade mapper;

    @Autowired
    public CategoryRestController(MapperFacade mapper,
                                  CategoryService categoryService,
                                  OrganizationService organizationService,
                                  SessionService sessionService,
                                  TagService tagService) {
        this.mapper = mapper;
        this.categoryService = categoryService;
        this.organizationService = organizationService;
        this.sessionService = sessionService;
        this.tagService = tagService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CategoryResource> createCategory(@AuthenticationPrincipal User user,
                                                           @RequestParam("organizationId") int organizationId,
                                                           @Valid @RequestBody CreateCategoryResource categoryResource) {
        List<Organization> organizations = organizationService.getOrganizationsOfMember(user.getUsername());
        if (organizations == null || organizations.size() == 0){
            throw new CanDoControllerRuntimeException("User isn't an owner of an organization");
        }
        if (!organizations.stream().anyMatch(o -> o.getOrganizationId() == organizationId)){
            throw new CanDoControllerRuntimeException(String.format("User isn't an owner of organization with %d", organizationId));
        }

        Organization organization = organizationService.getOrganizationById(organizationId);

        Category category = mapper.map(categoryResource, Category.class);

        List<Integer> listTagId = categoryResource.getListTagId();
        if (listTagId != null) {
            List<Tag> tags = new ArrayList<>();
            for (int id : listTagId) {
                tags.add(tagService.getTag(id));
            }

            category.setTags(tags);
        }

        category.setOrganization(organization);
        category = categoryService.addCategory(category);
        return new ResponseEntity<>(mapper.map(category, CategoryResource.class), HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CategoryResource>> getCategories(@RequestParam("organizationId") int organizationId) {
        List<Category> categories = categoryService.getCategoriesByOrganizationId(organizationId);
        return new ResponseEntity<>(mapper.mapAsList(categories, CategoryResource.class), HttpStatus.OK);
    }

    @RequestMapping(value = "/{categoryId}", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CategoryResource> getCategory(@PathVariable("categoryId") int categoryId) {
        Category category = categoryService.getCategoryById(categoryId);
        return new ResponseEntity<>(mapper.map(category, CategoryResource.class), HttpStatus.OK);
    }

    @RequestMapping(value = "/{categoryId}/sessions", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SessionResource>> getSessionsFromCategory(@PathVariable("categoryId") int categoryId) {
        List<Session> sessions = sessionService.getSessionsFromCategory(categoryId);
        List<SessionResource> sessionResources = new ArrayList<>();

        for (Session session : sessions) {
            SessionResource resource = null;
            if (session instanceof SynchronousSession) {
                resource = mapper.map(session, SynchronousSessionResource.class);
            } else if (session instanceof AsynchronousSession){
                resource = mapper.map(session, AsynchronousSessionResource.class);
            }
            if (resource != null){
                sessionResources.add(resource);
            }
        }
        return new ResponseEntity<>(sessionResources, HttpStatus.OK);
    }

    @RequestMapping(value = "/{categoryId}/tags", method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity addTagsToCategory(@AuthenticationPrincipal User user, @PathVariable("categoryId") int categoryId, @RequestBody List<Integer> tagIds) {
        Category category = categoryService.getCategoryById(categoryId);

        if (category.getOrganization().isOrganizer(user)){
            throw new CanDoControllerRuntimeException("User is not owner of category");
        }

        List<Tag> selectedTags = new ArrayList<>();
        for (int id : tagIds) {
            selectedTags.add(tagService.getTag(id));
        }
        List<Tag> tags = categoryService.addTagsToCategory(category, selectedTags);

        return new ResponseEntity(HttpStatus.CREATED);
    }


    @RequestMapping(value = "/{categoryId}", method = RequestMethod.PUT)
    public ResponseEntity setOrganizationName(@PathVariable("categoryId") int categoryId, @RequestParam(value="categoryName")String categoryName){
        Category category = categoryService.getCategoryById(categoryId);
        category.setName(categoryName);
        categoryService.updateCategory(category);
        CategoryResource resource = mapper.map(category, CategoryResource.class);

        return new ResponseEntity<>(resource,HttpStatus.OK);
    }
}
