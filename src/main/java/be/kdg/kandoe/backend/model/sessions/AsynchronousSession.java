package be.kdg.kandoe.backend.model.sessions;

import lombok.Data;

import javax.persistence.Entity;

/**
 * An AsynchronousSession means that not all users are active at the same time
 * sencondsBetweenMoves determines the maximum time between two moves
 */
@Entity
@Data
public class AsynchronousSession extends Session {
    private int secondsBetweenMoves;
}
