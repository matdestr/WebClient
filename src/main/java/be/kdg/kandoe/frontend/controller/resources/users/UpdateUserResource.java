package be.kdg.kandoe.frontend.controller.resources.users;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.hateoas.ResourceSupport;

/**
 * Resource for the update of an user
 */

@NoArgsConstructor
@Data
public class UpdateUserResource extends ResourceSupport {
    @NotEmpty(message = "{user.update.wrong.username}")
    private String username;
    @NotEmpty(message = "{user.update.wrong.name}")
    private String name;
    @NotEmpty(message = "{user.update.wrong.surname}")
    private String surname;
    @NotEmpty(message = "{user.update.wrong.email}")
    @Email(message = "{user.update.wrong.email-verification}")
    private String email;
    @NotEmpty(message = "{user.update.wrong.password-verification}")
    private String verifyPassword;
}
