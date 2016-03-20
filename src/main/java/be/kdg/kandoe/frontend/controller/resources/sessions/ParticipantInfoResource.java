package be.kdg.kandoe.frontend.controller.resources.sessions;

import be.kdg.kandoe.frontend.controller.resources.users.UserResource;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Resource for the {@link be.kdg.kandoe.backend.model.sessions.ParticipantInfo}
 */

@Data
@NoArgsConstructor
public class ParticipantInfoResource {
    private UserResource participant;
    private boolean joined;
    private boolean addedCardsCompleted;
    private boolean reviewingCardsCompleted;
}
