package be.kdg.kandoe.backend.service.api;

import be.kdg.kandoe.backend.model.cards.CardDetails;
import be.kdg.kandoe.backend.model.cards.CardPosition;
import be.kdg.kandoe.backend.model.cards.Comment;
import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.backend.model.users.User;

import java.util.Set;

public interface SessionGameService {
    void inviteUserForSession(Session session, User user);
    //void setOpenForJoining(Session session);
    void setUserJoined(Session session, User user);
    void setUserLeft(Session session, User user);
    //void confirmUsersJoined(Session session);

    void addCardDetails(Session session, User user, CardDetails cardDetails);

    void confirmAddedCards(Session session);
    
    void addComment(User user, CardDetails cardDetails, Comment comment);
    void confirmReviews(Session session);


    void startGame(Session session);
    Set<CardPosition> getCardPositions(Session session);
    void increaseCardPriority(Session session, User user, CardPosition cardPosition);
    
    void endGame(Session session);
}
