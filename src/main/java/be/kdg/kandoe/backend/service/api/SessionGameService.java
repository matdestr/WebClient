package be.kdg.kandoe.backend.service.api;

import be.kdg.kandoe.backend.model.cards.CardDetails;
import be.kdg.kandoe.backend.model.cards.CardPosition;
import be.kdg.kandoe.backend.model.sessions.ParticipantInfo;
import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.backend.model.users.User;

import java.util.Set;

/**
 * Interface contract for managing the business logic of a session game.
 * */
public interface SessionGameService {
    /**
     * Invite a user to join a session
     * @param session = The session they want to join
     * @param user = The user who wants to join the session
     */
    void inviteUserForSession(Session session, User user);
    
    /**
     * Invites a user which is not yet known to the application to a session.
     * 
     * @param email The email address of the user to invite.
     * */
    void inviteNonExisitingUserForSession(Session session, String email);
    
    /**
     * Confirms the invited user for a given session. Invites are no longer possible for that session
     * after confirming them.
     * 
     * @param session The session to confirm the invites for.
     * */
    void confirmInvitedUsers(Session session);
    
    /**
     * Notifies the service that an invited user has joined the session.
     * 
     * @param session The session the user joined
     * @param user The invited user that joined
     * */
    void setUserJoined(Session session, User user);
    
    /**
     * Notifies the service that a participant has left the session.
     * 
     * @param session The session the user left
     * @param user The user that left the session
     * */
    void setUserLeft(Session session, User user);

    /**
     * Confirms that the given user has confirmed their added cards for the given session.
     * Adding more cards is no longer possible for the user after calling this method.
     * 
     * @param session The session the user confirms the added cards for (if any)
     * @param user The user to confirm the added cards for
     * */
    void confirmUserAddedCards(Session session, User user);
    void confirmAddedCards(Session session);

    /**
     * Registers the the chosen cards of a user for a given session.
     * 
     * @param session The session the user chooses cards for
     * @param user The user that chooses the cards
     * @param cardDetailsList The chosen cards
     * */
    void chooseCards(Session session, User user, Set<CardDetails> cardDetailsList);
    void confirmChoosingCards(Session session);

    /**
     * Confirms a user's posted reviews for a given session.
     * 
     * @param session The session the user confirms their reviews for
     * @param user The user that confirms their reviews
     * */
    void confirmReviews(Session session, User user);

    /**
     * Calculates the next participant of a session based on the order in which the participants joined.
     * 
     * @param session The session to calculate the next participant for
     * */
    ParticipantInfo getNextParticipant(Session session);
    
    /**
     * Calculates the next participant for a session based on the order in which the participants joined
     * and the given participant as reference point.
     * 
     * Calling this method with the current session participant as second parameter yields the same
     * result as calling {@link #getNextParticipant}
     * 
     * @param session The session to calculate the next participant for
     * @param participantInfo The reference point from which the next participant has to be calculated.
     * */
    ParticipantInfo getNextParticipant(Session session, ParticipantInfo participantInfo);
    
    /**
     * Increases the priority of a given card on the circle. Increasing the priority of a card
     * is analog to moving the card towards the middle of the circle.
     * 
     * @param session The session to which the card belongs
     * @param user The user who wishes to move the card towards the middle of the circle
     * @param cardDetails The card to increase the priority for
     * */
    CardPosition increaseCardPriority(Session session, User user, CardDetails cardDetails);

    /**
     * Starts the game for a given session, meaning from now on, participants can play the game by moving
     * cards towards the middle of the circle.
     * 
     * @param session The session to start the game for.
     * */
    void startGame(Session session);
    
    /**
     * Ends the game for a given session and calculates the winning cards.
     * 
     * @param session The session to end the game for and calculate the winning cards for.
     * */
    void endGame(Session session);
}
