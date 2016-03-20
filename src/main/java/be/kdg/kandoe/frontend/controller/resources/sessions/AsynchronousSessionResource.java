package be.kdg.kandoe.frontend.controller.resources.sessions;


import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Resource for a {@link be.kdg.kandoe.backend.model.sessions.AsynchronousSession}
 */

@Data
@NoArgsConstructor
public class AsynchronousSessionResource extends SessionResource {
    private int secondsBetweenMoves;
}
