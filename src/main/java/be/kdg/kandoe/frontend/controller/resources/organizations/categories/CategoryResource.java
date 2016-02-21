package be.kdg.kandoe.frontend.controller.resources.organizations.categories;

import be.kdg.kandoe.frontend.controller.resources.organizations.OrganizationResource;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created on 20/02/2016
 *
 * @author Arne De Cock
 */
@Data
public class CategoryResource {
    private int categoryId;
    @NotEmpty
    private String name;
    private String description;
    private OrganizationResource organization;
}
