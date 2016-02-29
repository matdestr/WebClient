package be.kdg.kandoe.frontend.controller.resources.organizations.categories;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Data
@NoArgsConstructor
public class CreateCategoryResource {
    @NotEmpty
    private String name;
    @Length(min = 0, max = 1000)
    private String description;
}
