package be.kdg.kandoe.frontend.controller.resources.users;

import lombok.*;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class CreateUserResource {
    @NotNull
    @Length(min = 4, max = 25)
    private String username;
    
    @Length(min = 0, max = 100)
    private String name;
    
    @Length(min = 0, max = 100)
    private String surname;
    
    @NotNull
    private String password;
    
    @NotNull
    private String verifyPassword;
    
    @Email
    private String email;

    public CreateUserResource(String username, String password, String verifyPassword, String email) {
        this.username = username;
        this.password = password;
        this.verifyPassword = verifyPassword;
        this.email = email;
    }
    
    @AssertTrue(message = "Password verification should be equal to the filled in password")
    private boolean isValid() {
        return this.password.equals(this.verifyPassword);
    }
}
