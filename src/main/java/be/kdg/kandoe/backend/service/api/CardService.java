package be.kdg.kandoe.backend.service.api;

import be.kdg.kandoe.backend.model.cards.CardDetails;
import be.kdg.kandoe.backend.model.cards.CardPosition;
import be.kdg.kandoe.backend.model.organizations.Topic;
import be.kdg.kandoe.backend.model.sessions.Session;

import java.util.Set;

public interface CardService {
    CardDetails addCardDetailsToTopic(Topic topic, CardDetails cardDetails);

    Set<CardDetails> getCardDetailsOfTopic(int topicId);

    Set<CardDetails> getCardDetailsOfCategory(int categoryId);

    void initializeCardPositions(Session session);

    Set<CardPosition> getCardPositionsOfSession(int sessionId);
}
