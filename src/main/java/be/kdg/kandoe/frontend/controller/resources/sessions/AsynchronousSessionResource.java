package be.kdg.kandoe.frontend.controller.resources.sessions;


import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
public class AsynchronousSessionResource extends SessionResource {
    private int secondsBetweenMoves;
}
