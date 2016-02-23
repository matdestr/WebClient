package be.kdg.kandoe.backend.service.impl;

import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.backend.persistence.api.SessionRepository;
import be.kdg.kandoe.backend.service.api.SessionService;
import be.kdg.kandoe.backend.service.exceptions.SessionServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SessionServiceImpl implements SessionService {
    @Autowired
    private SessionRepository sessionRepository;

    @Override
    public Session getSessionById(int sessionId) {
        Session fetchedSession = sessionRepository.findOne(sessionId);
        if (fetchedSession == null){
            throw new SessionServiceException(String.format("No session found with id %d", sessionId));
        }
        return fetchedSession;
    }

    @Override
    public Session addSession(Session session) {
        Session savedSession = sessionRepository.save(session);
        if (savedSession == null) {
            throw new SessionServiceException("Session couldn't be saved");
        }
        return session;
    }
}
