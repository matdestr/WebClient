package be.kdg.kandoe.backend.persistence.api;

import be.kdg.kandoe.backend.model.cards.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Proxy interface for the CRUD Repository for the {@link Comment} model
 */

public interface CommentRepository extends JpaRepository<Comment, Integer> {
}
