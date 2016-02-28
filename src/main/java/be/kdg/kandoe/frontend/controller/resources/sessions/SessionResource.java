package be.kdg.kandoe.frontend.controller.resources.sessions;

import be.kdg.kandoe.frontend.controller.resources.users.UserResource;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor
@Data
public class SessionResource {
    private int sessionId;
    private int organizerId;
    private List<UserResource> participants = new ArrayList<>();
    private int topicId;
    private String url;
    private boolean commentsAllowed;
    private boolean userCanAddCards;
    private int minNumberOfCards;
    private int maxNumberOfCards;
    private boolean finished;
}
