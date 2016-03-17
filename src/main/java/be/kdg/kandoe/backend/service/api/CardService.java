/**
 * Interface to manage cards
 */
package be.kdg.kandoe.backend.service.api;

import be.kdg.kandoe.backend.model.cards.CardDetails;
import be.kdg.kandoe.backend.model.cards.Comment;
import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Topic;
import be.kdg.kandoe.backend.model.users.User;

import java.util.Set;

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
}
