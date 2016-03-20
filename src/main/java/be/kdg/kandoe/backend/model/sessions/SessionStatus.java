package be.kdg.kandoe.backend.model.sessions;

/**
 * The possible statuses a session can have, in chronological order
 */
public enum SessionStatus {
    CREATED,
    USERS_JOINING,
    ADDING_CARDS,
    REVIEWING_CARDS,
    CHOOSING_CARDS,
    READY_TO_START,
    IN_PROGRESS,
    FINISHED
}
