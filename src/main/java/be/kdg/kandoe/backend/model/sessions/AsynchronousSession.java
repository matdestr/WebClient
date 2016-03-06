package be.kdg.kandoe.backend.model.sessions;

import lombok.Data;

import javax.persistence.Entity;

@Entity
@Data
public class AsynchronousSession extends Session {
    private int secondsBetweenMoves;
}
