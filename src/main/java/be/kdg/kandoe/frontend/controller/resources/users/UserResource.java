package be.kdg.kandoe.frontend.controller.resources.users;

import be.kdg.kandoe.backend.model.users.roles.RoleType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;

@NoArgsConstructor
@Data
public class UserResource extends ResourceSupport {
    private int userId;
    private String username;
    private String name;
    private String surname;
    private String email;
    private String profilePictureUrl;
    private List<RoleType> roleTypes;
}
