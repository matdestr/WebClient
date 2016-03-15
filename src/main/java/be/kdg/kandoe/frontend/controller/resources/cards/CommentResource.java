package be.kdg.kandoe.frontend.controller.resources.cards;

import be.kdg.kandoe.frontend.controller.resources.users.UserResource;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CommentResource {
    private int commentId;
    private String message;
    private UserResource user;
    private LocalDateTime datePosted;
}
