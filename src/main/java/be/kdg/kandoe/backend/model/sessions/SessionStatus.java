/**
 * The status of a session in the right order
 */
package be.kdg.kandoe.backend.model.sessions;

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
