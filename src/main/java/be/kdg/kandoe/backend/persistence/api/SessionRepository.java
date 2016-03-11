package be.kdg.kandoe.backend.persistence.api;

import be.kdg.kandoe.backend.model.sessions.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Integer> {
    List<Session> findSessionsByParticipantInfo_Participant_UserId(@Param("userId") int userId);
    List<Session> findSessionsByCategoryCategoryId(int categoryId);
    List<Session> findSessionsByTopicTopicId(int topicId);
}
