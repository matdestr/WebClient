package be.kdg.kandoe.backend.service.api;

import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.service.exceptions.CategoryServiceException;

/**
 * Created on 19/02/2016
 *
 * @author Arne De Cock
 */
public interface CategoryService {
    Category addCategory(Category category) throws CategoryServiceException;
    Category getCategoryByName(String categoryName, Organization organization);
}
