package be.kdg.kandoe.backend.service.api;


import be.kdg.kandoe.backend.model.sessions.Session;

import java.util.List;

public interface SessionService {
    Session getSessionById(int sessionId);
    Session addSession(Session session);
    Session updateSession(Session session);
    List<Session> getSessionsFromCategory(int categoryId);
    List<Session> getSessionsFromTopic(int topicId);
    //void updateSession(Session session);
    //void endSession(int sessionId);
}
