package be.kdg.kandoe.backend.model.sessions;

public class AsynchronousSession extends Session {
    //@todo TimeSpan maxAllowedTimeUntilNextMove

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
