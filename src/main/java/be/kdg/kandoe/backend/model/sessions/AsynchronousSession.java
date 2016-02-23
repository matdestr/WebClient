package be.kdg.kandoe.backend.model.sessions;

import javax.persistence.Entity;

@Entity
public class AsynchronousSession extends Session {
    //@todo TimeSpan maxAllowedTimeUntilNextMove
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
