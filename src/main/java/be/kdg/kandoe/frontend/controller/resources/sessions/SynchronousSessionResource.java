package be.kdg.kandoe.frontend.controller.resources.sessions;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Resource for the {@link SessionResource}
 */

@Data
@NoArgsConstructor
public class SynchronousSessionResource extends SessionResource {
    private LocalDateTime startDateTime;
}
