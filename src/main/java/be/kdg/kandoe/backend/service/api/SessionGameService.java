package be.kdg.kandoe.backend.service.api;

import be.kdg.kandoe.backend.model.cards.CardDetails;
import be.kdg.kandoe.backend.model.cards.CardPosition;
import be.kdg.kandoe.backend.model.cards.Comment;
import be.kdg.kandoe.backend.model.sessions.ParticipantInfo;
import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.backend.model.users.User;

import java.util.List;
import java.util.Set;

public interface SessionGameService {
    void inviteUserForSession(Session session, User user);
    //void setOpenForJoining(Session session);
    void setUserJoined(Session session, User user);
    void setUserLeft(Session session, User user);
    //void confirmUsersJoined(Session session);

    void confirmUserAddedCards(Session session, User user);
    void confirmAddedCards(Session session);

    //void chooseCards(Session session, User user, CardDetails cardDetails);
    void chooseCards(Session session, User user, Set<CardDetails> cardDetailsList);
    //void confirmCardsChosen(Session session, User user);
    void confirmChoosingCards(Session session);

    void addReview(User user, CardDetails cardDetails, Comment comment);
    void confirmReviews(Session session);

    ParticipantInfo getNextParticipant(Session session);

    CardPosition increaseCardPriority(Session session, User user, CardDetails cardDetails);

    void startGame(Session session);
    void endGame(Session session);
}
