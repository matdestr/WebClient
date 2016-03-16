package be.kdg.kandoe.frontend.controller.resources.organizations.categories;

import be.kdg.kandoe.backend.model.organizations.Tag;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CategoryResource {
    private int categoryId;
    private String name;
    private String description;
    private int organizationId;
    private List<TagResource> tags;

}
