package be.kdg.kandoe.frontend.controller.resources.organizations.categories;

import be.kdg.kandoe.frontend.controller.resources.organizations.OrganizationResource;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoryResource {
    private int categoryId;
    private String name;
    private String description;
    private List<TagResource> tagResources;
    private int organizationId;
}
