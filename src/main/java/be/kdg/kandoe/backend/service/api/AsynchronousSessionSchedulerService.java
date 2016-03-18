package be.kdg.kandoe.backend.service.api;

import be.kdg.kandoe.backend.model.sessions.AsynchronousSession;
import be.kdg.kandoe.backend.model.users.User;

/** 
 * This service is responsible for assigning the next participant and notifying the current
 * participant of an asynchronous session when a participant's turn is about to expire.
 * */
public interface AsynchronousSessionSchedulerService {
    /**
     * Sends a notification to the user when their turn for the session is about to expire.
     * 
     * @param session The {@link AsynchronousSession} to notify the user for
     * @param user The {@link User} to send the notification to
     * */
    void scheduleParticipantNotificationTurnAboutToExpire(AsynchronousSession session, User user);
    
    /**
     * Cancels the scheduled notification for the specified user.
     * This method should be called when the participant moved a card on the circle
     * within the given timespan.
     * 
     * @param session The {@link AsynchronousSession} the user is a participant of
     * @param user The {@link User} to cancel the notification for
     * */
    void cancelParticipantNotification(AsynchronousSession session, User user);
    
    /**
     * Schedules the action of assigning the next participant of the session when the
     * current participant's turn expires.
     * 
     * @param session The {@link AsynchronousSession} to assign the next participant for
     * */
    void scheduleNextParticipantAssignment(AsynchronousSession session);
    
    /**
     * Cancels the scheduled action of assigning the next participant of the session.
     * 
     * @param session The {@link AsynchronousSession} to cancel the participant assignment action for
     * */
    void cancelNextParticipantAssignment(AsynchronousSession session);
}
