package be.kdg.kandoe.frontend.controller.resources.sessions.create;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import javax.annotation.Nullable;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class CreateAsynchronousSessionResource extends CreateSessionResource {
    @Range(min = 0)
    private int timeBetweenMoves;
}
