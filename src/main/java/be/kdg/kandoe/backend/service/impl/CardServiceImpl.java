package be.kdg.kandoe.backend.service.impl;

import be.kdg.kandoe.backend.model.cards.CardDetails;
import be.kdg.kandoe.backend.model.cards.CardPosition;
import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Topic;
import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.backend.persistence.api.CardDetailsRepository;
import be.kdg.kandoe.backend.persistence.api.CategoryRepository;
import be.kdg.kandoe.backend.persistence.api.TopicRepository;
import be.kdg.kandoe.backend.service.api.CardService;
import be.kdg.kandoe.backend.service.exceptions.CardServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class CardServiceImpl implements CardService {
    private CardDetailsRepository cardDetailsRepository;
    private CategoryRepository categoryRepository;
    private TopicRepository topicRepository;

    private Logger logger;

    @Autowired
    public CardServiceImpl(CardDetailsRepository cardDetailsRepository, CategoryRepository categoryRepository, TopicRepository topicRepository) {
        this.cardDetailsRepository = cardDetailsRepository;
        this.categoryRepository = categoryRepository;
        this.topicRepository = topicRepository;
        this.logger = LogManager.getLogger(this.getClass());
    }

    @Override
    public CardDetails addCardDetailsToCategory(Category category, CardDetails cardDetails) {
        if (category == null) {
            logger.warn("Received null instead of valid category");
            throw new CardServiceException("Category cannot be null");
        }

        cardDetails.setCategory(category);

        this.validateCardDetails(cardDetails);
        this.validateCardDetailsExistence(category, cardDetails);

        try {
            cardDetails.setCategory(category);

            cardDetails = cardDetailsRepository.save(cardDetails);

            // Updating both sides of the relationship is needed
            if (category.getCards() == null)
                category.setCards(new HashSet<>());

            category.getCards().add(cardDetails);
            categoryRepository.save(category);

            return cardDetails;
        } catch (Exception e) {
            logger.warn("Could not persist card details: " + e.getMessage());
            throw new CardServiceException("Could not save card details");
        }
    }

    @Override
    public CardDetails addCardDetailsToTopic(Topic topic, CardDetails cardDetails) {
        if (topic == null) {
            logger.warn("Received null instead of valid topic");
            throw new CardServiceException("Topic cannot be null");
        }

        this.validateCardDetails(cardDetails);
        this.validateCardDetailsExistence(topic, cardDetails);

        if(cardDetails.getCategory() == null){
            this.addCardDetailsToCategory(topic.getCategory(),cardDetails);
        }
        if (!cardDetails.getCategory().equals(topic.getCategory())) {
            logger.warn("Tried to add card details to topic, but topic was of different category");
            throw new CardServiceException("Cannot add card details to topics of different categories");
        }

        if (cardDetails.getTopics() == null)
            cardDetails.setTopics(new HashSet<>());

        Set<Topic> cardDetailsTopics = cardDetails.getTopics();
        cardDetailsTopics.add(topic);

        try {
            cardDetails = cardDetailsRepository.save(cardDetails);

            if (topic.getCategory().getCards() == null)
                topic.getCategory().setCards(new HashSet<>());

            if (topic.getCards() == null)
                topic.setCards(new HashSet<>());

            topic.getCategory().getCards().add(cardDetails);
            topic.getCards().add(cardDetails);

            categoryRepository.save(topic.getCategory());
            topicRepository.save(topic);

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
    public CardDetails getCardDetailsById(int cardDetailsId) {
        return cardDetailsRepository.findOne(cardDetailsId);
    }



    private void validateCardDetails(CardDetails cardDetails) {
        if (cardDetails == null) {
            logger.warn("Received null instead of valid card details");
            throw new CardServiceException("Card details cannot be null");
        }

        if (cardDetails.getCreator() == null) {
            logger.warn("Received card details without a creator");
            throw new CardServiceException("Card details creator cannot be null");
        }
    }

    private void validateCardDetailsExistence(Category category, CardDetails cardDetails) {
        CardDetails cardDetailsMatchingTextAndCategory =
                cardDetailsRepository.findCardDetailsByCategoryAndText(category, cardDetails.getText());

        if (cardDetailsMatchingTextAndCategory != null) {
            logger.warn("User tried to create card details with already existing text");
            throw new CardServiceException("A card with that text already exists in the category");
        }

        if (cardDetails.getCategory() != null && !cardDetails.getCategory().equals(category)) {
            logger.warn("User tried to add card details to multiple categories");
            throw new CardServiceException("Cannot add the same card details to multiple categories");
        }
    }

    private void validateCardDetailsExistence(Topic topic, CardDetails cardDetails) {
        CardDetails cardDetailsMatchingTextAndCategory =
                cardDetailsRepository.findCardDetailsByTopicAndText(topic, cardDetails.getText());

        if (cardDetailsMatchingTextAndCategory != null) {
            logger.warn("User tried to create card details with already existing text");
            throw new CardServiceException("A card with that text already exists in the topic");
        }
    }

}
