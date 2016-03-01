package be.kdg.kandoe.frontend.controller.resources.organizations.categories;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoryResource {
    private int categoryId;
    private String name;
    private String description;
    private int organizationId;
}
