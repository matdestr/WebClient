package be.kdg.kandoe.frontend.controller.rest;

import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.backend.model.sessions.SynchronousSession;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.CategoryService;
import be.kdg.kandoe.backend.service.api.OrganizationService;
import be.kdg.kandoe.backend.service.api.SessionService;
import be.kdg.kandoe.frontend.controller.resources.organizations.categories.CategoryResource;
import be.kdg.kandoe.frontend.controller.resources.organizations.categories.CreateCategoryResource;
import be.kdg.kandoe.frontend.controller.resources.sessions.AsynchronousSessionResource;
import be.kdg.kandoe.frontend.controller.resources.sessions.SessionResource;
import be.kdg.kandoe.frontend.controller.resources.sessions.SynchronousSessionResource;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryRestController {

    private final CategoryService categoryService;
    private final OrganizationService organizationService;
    private final SessionService sessionService;

    private MapperFacade mapper;

    @Autowired
    public CategoryRestController(MapperFacade mapper,
                                  CategoryService categoryService,
                                  OrganizationService organizationService,
                                  SessionService sessionService) {
        this.mapper = mapper;
        this.categoryService = categoryService;
        this.organizationService = organizationService;
        this.sessionService = sessionService;
    }

    @RequestMapping(method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CategoryResource> createCategory(@RequestParam("organizationId") int organizationId,
                                                                 @Valid @RequestBody CreateCategoryResource categoryResource) {
        Organization organization = organizationService.getOrganizationById(organizationId);
        Category category = mapper.map(categoryResource, Category.class);
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

        //return new ResponseEntity<>(mapper.mapAsList(sessions, SessionResource.class), HttpStatus.OK);
        return new ResponseEntity<>(sessionResources, HttpStatus.OK);
    }
}
