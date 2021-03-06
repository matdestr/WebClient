package be.kdg.kandoe.backend.service.api;


import be.kdg.kandoe.backend.model.sessions.Session;

import java.util.List;

/**
 * Interface contract for the {@link Session} model
 */

public interface SessionService {
    Session getSessionById(int sessionId);
    Session addSession(Session session);
    List<Session> getSessionsUser(int userId);
    Session updateSession(Session session);
    List<Session> getSessionsFromCategory(int categoryId);
    List<Session> getSessionsFromTopic(int topicId);
}
