package be.kdg.kandoe.frontend.controller.resources.organizations;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Resource for an email
 */


@lombok.Value
public class EmailResource {
    @NotEmpty(message = "{email.wrong.email}")
    @Email(message = "{email.invalid.email}")
    private String email;
}
