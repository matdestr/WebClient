/**
 * Interface to manage categories
 */
package be.kdg.kandoe.backend.service.api;

import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.organizations.Tag;
import be.kdg.kandoe.backend.service.exceptions.CategoryServiceException;

import java.util.List;

public interface CategoryService {
    Category addCategory(Category category) throws CategoryServiceException;

    /**
     * Get all categories of one organization
     * @param organization = The organizaton upon where the category is added
     * @return
     */
    List<Category> getCategoriesByOrganizationId(int organization);

    Category getCategoryById(int catgoryId);
    Category getCategoryByName(String categoryName, Organization organization);
    void updateCategory(Category category) throws CategoryServiceException;
    List<Tag> addTagsToCategory(Category category, List<Tag> tags);
}