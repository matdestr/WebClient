package be.kdg.kandoe.backend.service.api;

import be.kdg.kandoe.backend.model.cards.Card;

public interface CardService {
    Card addCard(Card card);
    Card findCardById(int cardId);
}
