package be.kdg.kandoe.backend.persistence.api;

import be.kdg.kandoe.backend.model.cards.Comment;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CommentRepository extends JpaRepository<Comment, Integer> {
}
