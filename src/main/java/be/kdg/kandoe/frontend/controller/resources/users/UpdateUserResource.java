package be.kdg.kandoe.frontend.controller.resources.users;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.hateoas.ResourceSupport;

@NoArgsConstructor
@Data
public class UpdateUserResource extends ResourceSupport {
    @NotEmpty
    private String username;
    @NotEmpty
    private String name;
    @NotEmpty
    private String surname;
    @Email
    private String email;
    @NotEmpty
    private String verifyPassword;
}
