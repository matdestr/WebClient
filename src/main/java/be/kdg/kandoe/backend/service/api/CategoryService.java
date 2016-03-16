package be.kdg.kandoe.backend.service.api;

import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.organizations.Tag;
import be.kdg.kandoe.backend.service.exceptions.CategoryServiceException;

import java.util.List;

public interface CategoryService {
    Category addCategory(Category category) throws CategoryServiceException;
    List<Category> getCategoriesByOrganizationId(int organization);
    Category getCategoryById(int catgoryId);
    Category getCategoryByName(String categoryName, Organization organization);
    List<Tag> addTagsToCategory(Category category, List<Tag> tags);
}