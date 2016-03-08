package be.kdg.kandoe.frontend.controller.resources.organizations;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Data
public class CreateOrganizationResource {
    @NotEmpty(message = "{organization.wrong.name}")
    private String name;
    @Valid
    private List<EmailResource> emails = new ArrayList<>();
}
