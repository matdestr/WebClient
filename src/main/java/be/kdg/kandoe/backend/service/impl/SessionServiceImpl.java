package be.kdg.kandoe.backend.service.impl;

import be.kdg.kandoe.backend.model.sessions.ParticipantInfo;
import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.backend.model.sessions.SessionStatus;
import be.kdg.kandoe.backend.persistence.api.SessionRepository;
import be.kdg.kandoe.backend.service.api.SessionService;
import be.kdg.kandoe.backend.service.exceptions.SessionServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        session = validateSession(session);

        ParticipantInfo participantInfoOrganizer = new ParticipantInfo();
        participantInfoOrganizer.setParticipant(session.getOrganizer());
        participantInfoOrganizer.setJoined(false);
        
        session.getParticipantInfo().add(participantInfoOrganizer);
        session.setSessionStatus(SessionStatus.CREATED);
        Session savedSession;
        
        try {
            savedSession = sessionRepository.save(session);
        } catch (Exception e) {
            throw new SessionServiceException("Session could not be saved");
        }
        
        if (savedSession == null) {
            throw new SessionServiceException("Session couldn't be saved");
        }
        return savedSession;
    }

    @Override
    public Session updateSession(Session session) {
        session = validateSession(session);
        Session updatedSession = sessionRepository.save(session);
        if (updatedSession == null) {
            throw new SessionServiceException("Session couldn't be saved");
        }
        return updatedSession;
    }

    private Session validateSession(Session session){
        if (session.getTopic() != null) {
            if (session.getTopic().getCards().size() <= session.getMinNumberOfCardsPerParticipant()) {
                throw new SessionServiceException("Cannot create a session with less cards than the minimum required per participant");
            }
        } else {
            if (session.getCategory() == null)
                throw new SessionServiceException("Session must be linked to a category");

            if (session.getCategory().getCards() == null || session.getCategory().getCards().size() < session.getMinNumberOfCardsPerParticipant())
                throw new SessionServiceException("Cannot create a session with less cards than the minimum required per participant");
        }

        if (session.getOrganizer() == null)
            throw new SessionServiceException("Cannot add a session without an organizer");

        if (!session.getCategory().getOrganization().isOrganizer(session.getOrganizer()))
            throw new SessionServiceException("User must be organization organizer to be able to create a session");

        if (session.getMinNumberOfCardsPerParticipant() > session.getMaxNumberOfCardsPerParticipant())
            throw new SessionServiceException("Minimum amount of cards per participant must be greater than maximum amount of cards per participant");

        if (session.getAmountOfCircles() < Session.MIN_CIRCLE_AMOUNT)
            session.setAmountOfCircles(Session.MIN_CIRCLE_AMOUNT);

        if (session.getAmountOfCircles() > Session.MAX_CIRCLE_AMOUNT)
            session.setAmountOfCircles(Session.MAX_CIRCLE_AMOUNT);

        return session;
    }

    @Override
    public List<Session> getSessionsFromCategory(int categoryId) {
        List<Session> sessionList = sessionRepository.findSessionsByCategoryCategoryId(categoryId);

        if(sessionList.isEmpty()){
            throw new SessionServiceException(String.format("No sessions found for category id %d", categoryId));
        }

        return sessionList;
    }
}
