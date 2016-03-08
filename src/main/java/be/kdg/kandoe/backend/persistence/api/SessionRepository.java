package be.kdg.kandoe.backend.persistence.api;

import be.kdg.kandoe.backend.model.sessions.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Integer> {
    List<Session> findSessionsByCategoryCategoryId(int categoryId);
}
