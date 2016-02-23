package be.kdg.kandoe.backend.persistence.api;

import be.kdg.kandoe.backend.model.sessions.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Integer> {
}
