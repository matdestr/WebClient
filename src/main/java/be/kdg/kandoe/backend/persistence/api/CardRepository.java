package be.kdg.kandoe.backend.persistence.api;

import be.kdg.kandoe.backend.model.cards.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Integer>{
}
