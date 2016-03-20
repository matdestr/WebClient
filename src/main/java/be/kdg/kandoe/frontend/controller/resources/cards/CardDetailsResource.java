package be.kdg.kandoe.frontend.controller.resources.cards;

import be.kdg.kandoe.backend.model.cards.CardDetails;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Resource model for the {@link CardDetails} model
 */

@Data
@NoArgsConstructor
public class CardDetailsResource {
    private int cardDetailsId;
    private String text;
    private String imageUrl;
    private List<CommentResource> comments;
}
