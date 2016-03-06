package be.kdg.kandoe.backend.model.sessions;

import lombok.Data;

import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@Data
public class SynchronousSession extends Session {
    private LocalDateTime startDateTime;
}
