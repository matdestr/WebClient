package be.kdg.kandoe.backend.service.api;

import be.kdg.kandoe.backend.model.cards.CardDetails;
import be.kdg.kandoe.backend.model.cards.CardPosition;
import be.kdg.kandoe.backend.model.cards.Comment;
import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Topic;
import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.backend.model.users.User;

import java.util.List;
import java.util.Set;

/**
 * Interface contract for service of the {@link CardDetails} model
 */
public interface CardService {
    CardDetails addCardDetailsToCategory(Category category, CardDetails cardDetails);
    
    CardDetails addCardDetailsToTopic(Topic topic, CardDetails cardDetails);
    
    Set<CardDetails> getCardDetailsOfCategory(int categoryId);

    Set<CardDetails> getCardDetailsOfTopic(int topicId);
    
    CardDetails getCardDetailsById(int cardDetailsId);

    /**
     * Adding a comment to a card during REVIEWING_CARDS status of session
     * @param user = The person writing the comment
     * @param cardDetails = The card upon which the comment is added
     * @param message = The message of the comment
     */
    Comment addReview(User user, CardDetails cardDetails, String message);

    List<CardDetails> addAllCardDetailsToTopic(Topic topic, List<CardDetails> allCardDetails);

    List<CardDetails> addAllCardDetailsToCategory(Category category, List<CardDetails> allCardDetails);

    //void initializeCardPositions(Session session);

    //Set<CardPosition> getCardPositionsOfSession(int sessionId);
}
