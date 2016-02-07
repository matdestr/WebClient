package be.kdg.kandoe.backend.model.sessions;


import be.kdg.kandoe.backend.model.users.User;

import java.util.List;

public abstract class Session {
    private List<ChatMessage> chatMessages;
    private User organizer;
    private List<User> participants;
    public abstract String getPublicUrl();
    public abstract boolean commentAllowed();
    public abstract boolean usersCanAddCards();
    public abstract int getMinNumberOfCards();
    public abstract int getMaxNumberOfCards();
    public abstract void endSession();
    public abstract boolean isFinished();
}
