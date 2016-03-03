package be.kdg.kandoe.backend.service.impl;

import be.kdg.kandoe.backend.model.cards.CardDetails;
import be.kdg.kandoe.backend.model.cards.CardPosition;
import be.kdg.kandoe.backend.model.organizations.Topic;
import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.backend.persistence.api.CardDetailsRepository;
import be.kdg.kandoe.backend.service.api.CardService;
import be.kdg.kandoe.backend.service.exceptions.CardServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class CardServiceImpl implements CardService {
    private CardDetailsRepository cardDetailsRepository;
    
    private Logger logger;

    @Autowired
    public CardServiceImpl(CardDetailsRepository cardDetailsRepository) {
        this.cardDetailsRepository = cardDetailsRepository;
        this.logger = LogManager.getLogger(this.getClass());
    }
    
    /*@Override
    public Card addCard(Card card) {
        Card savedCard = cardRepository.save(card);
        
        if (savedCard == null){
            throw new CardServiceException("Card cannot be saved");
        }
        
        return savedCard;
    }*/

    @Override
    public CardDetails addCardDetailsToTopic(Topic topic, CardDetails cardDetails) {
        if (topic == null) {
            logger.warn("Received null instead of valid topic");
            throw new CardServiceException("Topic cannot be null");
        }
        
        if (cardDetails == null) {
            logger.warn("Received null instead of valid card details");
            throw new CardServiceException("Card details cannot be null");
        }
        
        if (cardDetails.getCreator() == null) {
            logger.warn("Received card details without a creator");
            throw new CardServiceException("Card details creator cannot be null");
        }
        
        CardDetails cardDetailsMatchingTextAndCategory = 
                cardDetailsRepository.findCardDetailsByCategoryAndText(topic.getCategory(), cardDetails.getText());
        
        if (cardDetailsMatchingTextAndCategory != null) {
            logger.warn("User tried to create card details with already existing text");
            throw new CardServiceException("A card with that text already exists in the category");
        }
        
        Set<Topic> topics = cardDetails.getTopics();
        
        if (topics == null) {
            topics = new HashSet<>();
        } else {
            Topic existingTopic = topics.iterator().next();
            
            if (!existingTopic.getCategory().equals(topic.getCategory())) {
                logger.warn("Tried to add card details to topic, but topic was of different category");
                throw new CardServiceException("Cannot add card details to topics of different categories");
            }
        }
        
        topics.add(topic);
        cardDetails.setTopics(topics);
        
        try {
            cardDetails = cardDetailsRepository.save(cardDetails);
            return cardDetails;
        } catch (Exception e) {
            logger.warn("Could not persist card details: " + e.getMessage());
            throw new CardServiceException("Could not save card details");
        }
    }

    @Override
    public Set<CardDetails> getCardDetailsOfTopic(int topicId) {
        return cardDetailsRepository.findCardDetailsByTopicId(topicId);
    }

    @Override
    public Set<CardDetails> getCardDetailsOfCategory(int categoryId) {
        return cardDetailsRepository.findCardDetailsByCategoryId(categoryId);
    }

    @Override
    public void initializeCardPositions(Session session) {

    }

    @Override
    public Set<CardPosition> getCardPositionsOfSession(int sessionId) {
        return null;
    }

    /*@Override
    public CardDetails addCardDetails(CardDetails cardDetails) {
        return null;
    }

    @Override
    public CardDetails getCardDetailsById(int cardDetailsId) {
        return null;
    }

    @Override
    public Card addCard(int topicId, int cardDetailsId) {
        Card card = new Card();
    }

    @Override
    public Card findCardById(int cardId) {
        return cardRepository.findOne(cardId);
    }*/
}
