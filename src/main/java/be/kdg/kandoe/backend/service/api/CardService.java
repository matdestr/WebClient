package be.kdg.kandoe.backend.service.api;

import be.kdg.kandoe.backend.model.cards.CardDetails;
import be.kdg.kandoe.backend.model.cards.CardPosition;
import be.kdg.kandoe.backend.model.cards.Comment;
import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Topic;
import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.backend.model.users.User;

import java.util.Set;

public interface CardService {
    CardDetails addCardDetailsToCategory(Category category, CardDetails cardDetails);
    
    CardDetails addCardDetailsToTopic(Topic topic, CardDetails cardDetails);
    
    Set<CardDetails> getCardDetailsOfCategory(int categoryId);

    Set<CardDetails> getCardDetailsOfTopic(int topicId);
    
    CardDetails getCardDetailsById(int cardDetailsId);

    Comment addReview(User user, CardDetails cardDetails, String message);

    //void initializeCardPositions(Session session);

    //Set<CardPosition> getCardPositionsOfSession(int sessionId);
}
