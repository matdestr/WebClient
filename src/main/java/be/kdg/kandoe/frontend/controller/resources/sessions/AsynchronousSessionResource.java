package be.kdg.kandoe.frontend.controller.resources.sessions;


import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.frontend.controller.resources.organizations.topic.TopicResource;
import be.kdg.kandoe.frontend.controller.resources.users.UserResource;

import java.util.List;

public class AsynchronousSessionResource {
    private int sessinoId;
    private UserResource owner;
    private List<User> participants;
    private TopicResource topic;
    private String url;
    private boolean commentsAllowed;
    private boolean userCanAddCards;
    private int minNumberOfCards;
    private int maxNumberOfCards;
    private boolean isFinished;
    private int timeBetweenMoves;
}
