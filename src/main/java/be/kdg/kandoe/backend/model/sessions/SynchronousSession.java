package be.kdg.kandoe.backend.model.sessions;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
public class SynchronousSession extends Session {
    @Getter
    @Setter
    private LocalDateTime startDateTime;

    @Override
    public String getPublicUrl() {
        return null;
    }
    
    @Override
    public void endSession() {

    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
