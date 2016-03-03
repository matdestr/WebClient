package be.kdg.kandoe.backend.service.impl;

import be.kdg.kandoe.backend.model.cards.Card;
import be.kdg.kandoe.backend.persistence.api.CardRepository;
import be.kdg.kandoe.backend.service.api.CardService;
import be.kdg.kandoe.backend.service.exceptions.CardServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CardServiceImpl implements CardService {

    @Autowired
    private CardRepository cardRepository;

    @Override
    public Card addCard(Card card) {
        Card savedCard = cardRepository.save(card);
        if (savedCard == null){
            throw new CardServiceException("Card cannot be saved");
        }
        return savedCard;
    }

    @Override
    public Card findCardById(int cardId) {
        return cardRepository.findOne(cardId);
    }
}
