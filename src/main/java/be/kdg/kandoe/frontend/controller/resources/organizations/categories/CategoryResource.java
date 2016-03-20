package be.kdg.kandoe.frontend.controller.resources.organizations.categories;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Resource for the creation of a category
 */

@Data
@NoArgsConstructor
public class CategoryResource {
    private int categoryId;
    private String name;
    private String description;
    private int organizationId;
    private List<TagResource> tags;

}
