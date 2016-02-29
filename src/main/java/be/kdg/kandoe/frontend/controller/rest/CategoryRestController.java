package be.kdg.kandoe.frontend.controller.rest;

import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.service.api.CategoryService;
import be.kdg.kandoe.backend.service.api.OrganizationService;
import be.kdg.kandoe.frontend.controller.resources.organizations.categories.CategoryResource;
import be.kdg.kandoe.frontend.controller.resources.organizations.categories.CreateCategoryResource;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryRestController {

    private final CategoryService categoryService;
    private final OrganizationService organizationService;

    private MapperFacade mapper;

    @Autowired
    public CategoryRestController(MapperFacade mapper,
                                  CategoryService categoryService,
                                  OrganizationService organizationService) {
        this.mapper = mapper;
        this.categoryService = categoryService;
        this.organizationService = organizationService;
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
}
