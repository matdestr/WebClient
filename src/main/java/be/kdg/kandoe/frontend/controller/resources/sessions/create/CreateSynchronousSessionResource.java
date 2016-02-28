package be.kdg.kandoe.frontend.controller.resources.sessions.create;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
public class CreateSynchronousSessionResource extends CreateSessionResource {
    public static final String TYPE = "SYNCH";

    @Range(min = 0)
    private int timeBetweenMoves;

    public CreateSynchronousSessionResource() {
        super(CreateSynchronousSessionResource.TYPE);
    }
}
