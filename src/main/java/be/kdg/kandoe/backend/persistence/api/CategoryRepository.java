package be.kdg.kandoe.backend.persistence.api;

import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created on 19/02/2016
 *
 * @author Arne De Cock
 */
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Category findCategoryByNameAndOrganization(String categoryName, Organization organization);

}
