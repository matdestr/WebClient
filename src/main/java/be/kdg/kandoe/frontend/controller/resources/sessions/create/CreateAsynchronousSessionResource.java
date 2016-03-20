package be.kdg.kandoe.frontend.controller.resources.sessions.create;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

/**
 * Session resource for the creation of a {@link be.kdg.kandoe.backend.model.sessions.AsynchronousSession}
 */

@Data
public class CreateAsynchronousSessionResource extends CreateSessionResource {
    public static final String TYPE = "async";
    
    @Range(min = 1, message = "{session.wrong.time-between-moves}")
    private int secondsBetweenMoves;

    public CreateAsynchronousSessionResource() {
        super(CreateSynchronousSessionResource.TYPE);
    }
}
