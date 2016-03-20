package be.kdg.kandoe.frontend.controller.resources.sessions;

import be.kdg.kandoe.frontend.controller.resources.cards.CardDetailsResource;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Resource for the {@link be.kdg.kandoe.backend.model.cards.CardPosition}
 */

@Data
@NoArgsConstructor
public class CardPositionResource {
    private int cardPositionId;
    private CardDetailsResource cardDetails;
    private int priority;
}
