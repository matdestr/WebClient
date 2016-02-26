package be.kdg.kandoe.frontend.controller.resources.sessions;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.annotation.Nullable;
import javax.validation.constraints.Size;

public class CreateAsynchronousSessionResource extends CreateSessionResource{
    @Range(min = 0)
    private int timeBetweenMoves;
}
