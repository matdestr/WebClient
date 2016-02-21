package be.kdg.kandoe.backend.service.api;

import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.service.exceptions.CategoryServiceException;

import java.util.List;

/**
 * Created on 19/02/2016
 *
 * @author Arne De Cock
 */
public interface CategoryService {
    Category addCategory(Category category, Organization organization) throws CategoryServiceException;
    List<Category> getCategoriesByOrganizationId(int organization);
    Category getCategoryById(int catgoryId);
    Category getCategoryByName(String categoryName, Organization organization);
}