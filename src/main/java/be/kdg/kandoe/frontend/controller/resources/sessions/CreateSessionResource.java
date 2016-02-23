package be.kdg.kandoe.frontend.controller.resources.sessions;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@NoArgsConstructor
@Data
public class CreateSessionResource {
    private Integer topicId;
    private int organizationId;
    private boolean commentsAllowed;
    private boolean userCanAddCards;
    @Range(min = 1, max = 100)
    private int minNumberOfCards;
    @Range(min = 1, max = 100)
    private int maxNumberOfCards;
}
