package be.kdg.kandoe.frontend.controller.resources.sessions.reviews;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * Resource for the creation of a card review
 */

@Data
@NoArgsConstructor
public class CreateCardReviewOverview {
    @Range(min = 1)
    private int cardDetailsId;

    @NotEmpty
    @NotNull
    private String message;
}
