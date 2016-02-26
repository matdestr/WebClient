package be.kdg.kandoe.backend.model.sessions;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
public class AsynchronousSession extends Session {
    @Getter
    @Setter
    private int timeBetweenMoves;

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
