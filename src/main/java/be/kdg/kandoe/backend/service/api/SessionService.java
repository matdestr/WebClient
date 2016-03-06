package be.kdg.kandoe.backend.service.api;


import be.kdg.kandoe.backend.model.sessions.Session;

public interface SessionService {
    Session getSessionById(int sessionId);
    Session addSession(Session session);
    //void updateSession(Session session);
    //void endSession(int sessionId);
}
