package be.kdg.kandoe.backend.service.impl;

import be.kdg.kandoe.backend.model.cards.CardDetails;
import be.kdg.kandoe.backend.model.cards.CardPosition;
import be.kdg.kandoe.backend.model.cards.Comment;
import be.kdg.kandoe.backend.model.sessions.ParticipantInfo;
import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.backend.model.sessions.SessionStatus;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.persistence.api.SessionRepository;
import be.kdg.kandoe.backend.service.api.EmailService;
import be.kdg.kandoe.backend.service.api.SessionGameService;
import be.kdg.kandoe.backend.service.exceptions.SessionGameServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class SessionGameServiceImpl implements SessionGameService {
    private SessionRepository sessionRepository;
    private EmailService emailService;

    @Autowired
    public SessionGameServiceImpl(SessionRepository sessionRepository, EmailService emailService) {
        this.sessionRepository = sessionRepository;
        this.emailService = emailService;
    }

    @Override
    public void inviteUserForSession(Session session, User user) {
        if (session.getSessionStatus() != SessionStatus.CREATED && session.getSessionStatus() != SessionStatus.USERS_JOINING)
            throw new SessionGameServiceException("Cannot invite user when the session has already started");
        
        if (session.getSessionStatus() == SessionStatus.CREATED)
            session.setSessionStatus(SessionStatus.USERS_JOINING);
        
        if (session.getParticipantInfo().stream().anyMatch(p -> p.getParticipant().getUserId() == user.getUserId()))
            throw new SessionGameServiceException("User has already been invited to join the session");
        
        ParticipantInfo participantInfo = new ParticipantInfo();
        participantInfo.setParticipant(user);
        
        session.getParticipantInfo().add(participantInfo);
        session = sessionRepository.save(session);
        emailService.sendSessionInvitationToUser(session, session.getOrganizer(), user);
    }

    /*@Override
    public void setOpenForJoining(Session session) {
        if (session.getSessionStatus() != SessionStatus.CREATED)
            throw new SessionGameServiceException("Cannot open session for joining, session must be in status CREATED");

        session.setSessionStatus(SessionStatus.USERS_JOINING);
        
        try {
            sessionRepository.save(session);
        } catch (Exception e) {
            throw new SessionGameServiceException("Could not update session status");
        }
    }*/

    @Override
    public void setUserJoined(Session session, User user) {
        if (session.getParticipantInfo().size() <= 1)
            throw new SessionGameServiceException("Cannot join a session to which no users are invited");
        
        Optional<ParticipantInfo> firstMatchingParticipantInfo = session.getParticipantInfo().stream().filter(p -> p.getParticipant().equals(user)).findFirst();
        
        if (!firstMatchingParticipantInfo.isPresent())
            throw new SessionGameServiceException("User cannot join session to which he is not invited");
        
        ParticipantInfo participantInfo = firstMatchingParticipantInfo.get();
        participantInfo.setJoined(true);
        
        session = sessionRepository.save(session);
        
        if (session.getParticipantInfo().stream().allMatch(p -> p.isJoined()))
            this.confirmUsersJoined(session);
    }

    @Override
    public void setUserLeft(Session session, User user) {
        throw new SessionGameServiceException("Method not implemented");
    }

    //@Override
    private void confirmUsersJoined(Session session) {
        if (session.getSessionStatus() != SessionStatus.USERS_JOINING) {
            if (session.getSessionStatus() == SessionStatus.CREATED)
                throw new SessionGameServiceException("Cannot start a session without any invited users");
            else
                throw new SessionGameServiceException("Cannot start an already started session");
        }
        
        if (session.getParticipantInfo().stream().allMatch(p -> p.isJoined())) {
            if (session.isParticipantsCanAddCards())
                session.setSessionStatus(SessionStatus.ADDING_CARDS);
            else if (session.isCardCommentsAllowed())
                session.setSessionStatus(SessionStatus.REVIEWING_CARDS);
            else
                session.setSessionStatus(SessionStatus.IN_PROGRESS);
        } else {
            throw new SessionGameServiceException("Cannot start a session when not all users have joined");
        }
    }

    @Override
    public void addCardDetails(User user, CardDetails cardDetails) {
        throw new SessionGameServiceException("Method not implemented");
    }

    @Override
    public void confirmAddedCards() {
        throw new SessionGameServiceException("Method not implemented");
    }

    @Override
    public void addComment(User user, CardDetails cardDetails, Comment comment) {
        throw new SessionGameServiceException("Method not implemented");
    }

    @Override
    public void confirmReviews() {
        throw new SessionGameServiceException("Method not implemented");
    }

    @Override
    public void startGame(Session session) {
        throw new SessionGameServiceException("Method not implemented");
    }

    @Override
    public Set<CardPosition> getCardPositions(Session session) {
        throw new SessionGameServiceException("Method not implemented");
    }

    @Override
    public void increaseCardPriority(Session session, User user, CardPosition cardPosition) {
        throw new SessionGameServiceException("Method not implemented");
    }

    @Override
    public void endGame(Session session) {
        throw new SessionGameServiceException("Method not implemented");
    }
}
