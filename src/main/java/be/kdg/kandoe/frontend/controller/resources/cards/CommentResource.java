package be.kdg.kandoe.frontend.controller.resources.cards;

import be.kdg.kandoe.frontend.controller.resources.users.UserResource;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentResource {
    private int commentId;
    private UserResource user;
    // TODO : Custom mapper for LocalDateTime ?
}
