package be.kdg.kandoe.backend.model.sessions;

import java.time.LocalDateTime;

public class SynchrousSession extends Session {
    private LocalDateTime startDateTime;

    @Override
    public String getPublicUrl() {
        return null;
    }

    @Override
    public boolean commentAllowed() {
        return false;
    }

    @Override
    public boolean usersCanAddCards() {
        return false;
    }

    @Override
    public int getMinNumberOfCards() {
        return 0;
    }

    @Override
    public int getMaxNumberOfCards() {
        return 0;
    }

    @Override
    public void endSession() {

    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
