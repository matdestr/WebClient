package be.kdg.kandoe.frontend.controller.resources.cards;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CardDetailsResource {
    private int cardDetailsId;
    private String text;
    private String imageUrl;
    private List<CommentResource> comments;
}
