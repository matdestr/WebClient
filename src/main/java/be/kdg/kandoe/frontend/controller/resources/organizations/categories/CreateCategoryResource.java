package be.kdg.kandoe.frontend.controller.resources.organizations.categories;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

/**
 * Resource for the creation of a category
 */

@Data
@NoArgsConstructor
public class CreateCategoryResource {
    @NotEmpty(message = "{category.wrong.name}")
    private String name;
    @NotEmpty(message = "{category.wrong.description}")
    @Length(min = 0, max = 1000, message = "{category.wrong.length.description}")
    private String description;

    private List<Integer> listTagId;
}
