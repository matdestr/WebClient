package be.kdg.kandoe.backend.service.api;

import be.kdg.kandoe.backend.model.cards.CardDetails;
import be.kdg.kandoe.backend.model.cards.CardPosition;
import be.kdg.kandoe.backend.model.cards.Comment;
import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Topic;
import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.backend.model.users.User;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface contract for service of the {@link CardDetails} model
 */
public interface CardService {
    /**
     * Adds information about a card to a category.
     * */
    CardDetails addCardDetailsToCategory(Category category, CardDetails cardDetails);
    
    /**
     * Adds information about a card to a topic.
     * */
    CardDetails addCardDetailsToTopic(Topic topic, CardDetails cardDetails);
    
    /**
     * Retrieves the cards the category contains.
     * */
    Set<CardDetails> getCardDetailsOfCategory(int categoryId);

    /**
     * Retrieves information about the cards a topic contains.
     * */
    Set<CardDetails> getCardDetailsOfTopic(int topicId);
    
    /**
     * Retrieves information about a specific card.
     * */
    CardDetails getCardDetailsById(int cardDetailsId);

    /**
     * Adding a comment to a card during REVIEWING_CARDS status of session
     * @param user The person writing the comment
     * @param cardDetails The card upon which the comment is added
     * @param message The message of the comment
     */
    Comment addReview(User user, CardDetails cardDetails, String message);
    
    /**
     * Adds a series of comments to cards.
     * */
    void addReviews(User user, Map<CardDetails, String> reviews);

    /**
     * Adds a series of {@link CardDetails} to the given topic.
     * 
     * @param topic The topic to add the cards to.
     * @param allCardDetails The list of {@link CardDetails} to add to the topic.
     * */
    List<CardDetails> addAllCardDetailsToTopic(Topic topic, List<CardDetails> allCardDetails);

    /**
     * Adds a series of {@link CardDetails} to the given category.
     * 
     * @param category The category to add the cards to.
     * @param allCardDetails The list of {@link CardDetails} to add to the category.
     * */
    List<CardDetails> addAllCardDetailsToCategory(Category category, List<CardDetails> allCardDetails);

    //void initializeCardPositions(Session session);

    //Set<CardPosition> getCardPositionsOfSession(int sessionId);
}
