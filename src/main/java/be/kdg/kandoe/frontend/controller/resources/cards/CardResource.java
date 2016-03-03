package be.kdg.kandoe.frontend.controller.resources.cards;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CardResource {
    private int cardId;
    private int categoryId;
    private int cardDetailId;
}
