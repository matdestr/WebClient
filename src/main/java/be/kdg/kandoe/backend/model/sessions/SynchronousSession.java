package be.kdg.kandoe.backend.model.sessions;

import lombok.Data;

import javax.persistence.Entity;
import java.time.LocalDateTime;

/**
 * A SynchronousSession means that all the users are active at the same time and has a startTime
 */
@Entity
@Data
public class SynchronousSession extends Session {
    private LocalDateTime startDateTime;
}
