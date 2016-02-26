package be.kdg.kandoe.frontend.controller.resources.sessions.create;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.AssertTrue;

@Data
@NoArgsConstructor
public class CreateSessionResource {
    private Integer topicId;
    @Range(min = 1)
    private int organizationId;
    private boolean commentsAllowed;
    private boolean userCanAddCards;
    @Range(min = 1, max = 100)
    private int minNumberOfCards;
    @Range(min = 1, max = 100)
    private int maxNumberOfCards;

    @AssertTrue(message = "Min number of cards should be lower than the max numbers of cards")
    private boolean isValid() {
        return minNumberOfCards <= maxNumberOfCards;
    }
}
