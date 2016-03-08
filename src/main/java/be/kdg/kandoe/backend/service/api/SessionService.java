package be.kdg.kandoe.backend.service.api;


import be.kdg.kandoe.backend.model.sessions.Session;

import java.util.List;

public interface SessionService {
    Session getSessionById(int sessionId);
    Session addSession(Session session);
    List<Session> getSessionsFromCategory(int categoryId);
    //void updateSession(Session session);
    //void endSession(int sessionId);
}
