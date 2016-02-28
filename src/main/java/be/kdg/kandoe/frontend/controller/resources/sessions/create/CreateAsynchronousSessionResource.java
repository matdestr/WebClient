package be.kdg.kandoe.frontend.controller.resources.sessions.create;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
public class CreateAsynchronousSessionResource extends CreateSessionResource {
    public static final String TYPE = "ASYNCH";
    @Range(min = 0)
    private int timeBetweenMoves;

    public CreateAsynchronousSessionResource() {
        super(CreateSynchronousSessionResource.TYPE);
    }
}
