package be.kdg.kandoe.frontend.controller.resources.users;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Resource for the {@link be.kdg.kandoe.backend.model.users.User} model
 */

@Data
@NoArgsConstructor
public class UserResource {
    private int userId;
    private String username;
    private String name = "";
    private String surname = "";
    private String email = "";
    private String profilePictureUrl = "";
}
