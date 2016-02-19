package be.kdg.kandoe.frontend.controller.resources.users;

import lombok.*;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class CreateUserResource {
    @NotNull(message = "{register.wrong.username}")
    @Length(min = 4, max = 25, message = "{register.wrong.length.username}")
    private String username;

    @Length(min = 0, max = 100, message = "{register.wrong.name}")
    private String name;

    @Length(min = 0, max = 100,message = "{register.wrong.surname}")
    private String surname;

    @NotNull(message = "{register.wrong.password}")
    private String password;

    @NotNull(message = "{register.wrong.verifypassword}")
    private String verifyPassword;

    @NotNull (message = "{register.wrong.email}")
    @Email(message = "{register.wrong.email-verification}")
    private String email;

    public CreateUserResource(String username, String password, String verifyPassword, String email) {
        this.username = username;
        this.password = password;
        this.verifyPassword = verifyPassword;
        this.email = email;
    }

    @AssertTrue(message = "{register.wrong.password-verification}")
    private boolean isValid() {
        return this.password.equals(this.verifyPassword);
    }
}
