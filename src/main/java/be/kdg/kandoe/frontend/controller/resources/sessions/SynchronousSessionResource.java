package be.kdg.kandoe.frontend.controller.resources.sessions;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class SynchronousSessionResource extends SessionResource {
    private LocalDateTime startTime;
}
