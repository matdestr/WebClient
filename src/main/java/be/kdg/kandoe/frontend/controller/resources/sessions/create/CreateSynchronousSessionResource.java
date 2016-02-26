package be.kdg.kandoe.frontend.controller.resources.sessions.create;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@NoArgsConstructor
@Data
public class CreateSynchronousSessionResource extends CreateSessionResource {
    @Range(min = 0)
    private int timeBetweenMoves;
}
