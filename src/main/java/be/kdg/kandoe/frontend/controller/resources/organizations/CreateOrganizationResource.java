package be.kdg.kandoe.frontend.controller.resources.organizations;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

/**
 * Created by Wannes on 29/02/16.
 */
@Data
public class CreateOrganizationResource {
    @NotEmpty
    private String name;
    private List<Email> emails;
}
