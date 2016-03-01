package be.kdg.kandoe.frontend.controller.resources.users;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class CreateUserResource {
    @NotEmpty(message = "{register.wrong.username}")
    @Length(min = 4, max = 25, message = "{register.wrong.length.username}")
    private String username;

    @Length(min = 0, max = 100, message = "{register.wrong.name}")
    private String name;

    @Length(min = 0, max = 100,message = "{register.wrong.surname}")
    private String surname;

    @NotEmpty(message = "{register.wrong.password}")
    private String password;

    @NotEmpty(message = "{register.wrong.verifypassword}")
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
        // Workaround: this is called before @NotNull checks (for some reason)
        // TODO : Search better fix
        if (this.password == null || this.verifyPassword == null)
            return false;
        
        if (this.password.isEmpty() || this.verifyPassword.isEmpty())
            return false;
        
        return this.password.equals(this.verifyPassword);
    }
}
