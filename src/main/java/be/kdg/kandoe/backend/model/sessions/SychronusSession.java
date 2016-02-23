package be.kdg.kandoe.backend.model.sessions;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
public class SychronusSession extends Session {
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
