/**
 * Interface to manage the SessionGames
 */
package be.kdg.kandoe.backend.service.api;

import be.kdg.kandoe.backend.model.cards.CardDetails;
import be.kdg.kandoe.backend.model.cards.CardPosition;
import be.kdg.kandoe.backend.model.sessions.ParticipantInfo;
import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.backend.model.users.User;

import java.util.Set;

public interface SessionGameService {
    /**
     * Invite a user to join a session
     * @param session = The session they want to join
     * @param user = The user who wants to join the session
     */
    void inviteUserForSession(Session session, User user);
    void confirmInvitedUsers(Session session);
    void setUserJoined(Session session, User user);
    void setUserLeft(Session session, User user);

    void confirmUserAddedCards(Session session, User user);
    void confirmAddedCards(Session session);

    void chooseCards(Session session, User user, Set<CardDetails> cardDetailsList);
    void confirmChoosingCards(Session session);

    void confirmReviews(Session session, User user);

    ParticipantInfo getNextParticipant(Session session);

    CardPosition increaseCardPriority(Session session, User user, CardDetails cardDetails);

    void startGame(Session session);
    void endGame(Session session);
}
