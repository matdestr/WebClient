package be.kdg.kandoe.backend.service.api;

import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.organizations.Tag;
import be.kdg.kandoe.backend.service.exceptions.CategoryServiceException;

import java.util.List;

/**
 * Interface contract for service of the {@link Category} model.
 */
public interface CategoryService {
    Category addCategory(Category category) throws CategoryServiceException;

    /**
     * Get all categories of one organization
     * @param organization = The organizaton upon where the category is added
     * @return
     */
    List<Category> getCategoriesByOrganizationId(int organization);

    /**
     * Retrieves a category by its given unique ID.
     * */
    Category getCategoryById(int catgoryId);
    
    /**
     * Retrieves a category (within an organization) by its given name.
     * 
     * @param categoryName The name of the category.
     * @param organization The organization the category belongs to.
     * */
    Category getCategoryByName(String categoryName, Organization organization);
    
    /**
     * Updates the information of a category.
     * 
     * @param category The category to update the state of.
     * */
    void updateCategory(Category category) throws CategoryServiceException;
    
    /**
     * Adds a series of tags to a given category.
     * 
     * @param category The category to add the tags to.
     * @param tags The tags to add to the given category.
     * */
    List<Tag> addTagsToCategory(Category category, List<Tag> tags);
}
