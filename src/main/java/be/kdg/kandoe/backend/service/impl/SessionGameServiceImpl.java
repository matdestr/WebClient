package be.kdg.kandoe.backend.service.impl;

import be.kdg.kandoe.backend.model.cards.CardDetails;
import be.kdg.kandoe.backend.model.cards.CardPosition;
import be.kdg.kandoe.backend.model.cards.Comment;
import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Topic;
import be.kdg.kandoe.backend.model.sessions.ParticipantInfo;
import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.backend.model.sessions.SessionStatus;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.persistence.api.SessionRepository;
import be.kdg.kandoe.backend.service.api.CardService;
import be.kdg.kandoe.backend.service.api.EmailService;
import be.kdg.kandoe.backend.service.api.SessionGameService;
import be.kdg.kandoe.backend.service.exceptions.SessionGameServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class SessionGameServiceImpl implements SessionGameService {
    private final CardService cardService;
    private final SessionRepository sessionRepository;
    private final EmailService emailService;

    @Autowired
    public SessionGameServiceImpl(SessionRepository sessionRepository, EmailService emailService, CardService cardService) {
        this.sessionRepository = sessionRepository;
        this.emailService = emailService;
        this.cardService = cardService;
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

        Optional<ParticipantInfo> firstMatchingParticipantInfo = session.getParticipantInfo().stream().filter(p -> p.getParticipant().getUserId() == user.getUserId()).findFirst();

        if (!firstMatchingParticipantInfo.isPresent())
            throw new SessionGameServiceException("User cannot join session to which he is not invited");

        ParticipantInfo participantInfo = firstMatchingParticipantInfo.get();
        participantInfo.setJoined(true);

        session = sessionRepository.save(session);

        if (session.getParticipantInfo().stream().allMatch(p -> p.isJoined()))
            this.confirmUsersJoined(session);
    }

    //TODO: testen
    @Override
    public void setUserLeft(Session session, User user) {
        if (isUserInSession(session, user)) {
            ParticipantInfo firstMatchingParticipantInfo = session.getParticipantInfo().stream().filter(p -> p.getParticipant().getUserId() == user.getUserId()).findFirst().get();
            firstMatchingParticipantInfo.setJoined(false);
            session = sessionRepository.save(session);
            if (session == null) {
                throw new SessionGameServiceException("Session couldn't be updated");
            }
        } else {
            throw new SessionGameServiceException("User not in session");
        }
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
                session.setSessionStatus(SessionStatus.CHOOSING_CARDS);
        } else {
            throw new SessionGameServiceException("Cannot start a session when not all users have joined");
        }
    }

    @Override
    public void addCardDetails(Session session, User user, CardDetails cardDetails) {
        if (session.isParticipantsCanAddCards()) {
            if (session.getSessionStatus() == SessionStatus.ADDING_CARDS) {
                if (isUserInSession(session, user)) {
                    cardDetails.setCreator(user);
                    if (session.getTopic() == null) {
                        Category category = session.getCategory();
                        cardDetails = cardService.addCardDetailsToCategory(category, cardDetails);
                    } else {
                        Topic topic = session.getTopic();
                        cardDetails = cardService.addCardDetailsToTopic(topic, cardDetails);
                    }
                } else {
                    throw new SessionGameServiceException("User not in session");
                }
            } else {
                throw new SessionGameServiceException("Session not in adding cards modus");
            }
        } else {
            throw new SessionGameServiceException("Session doesn't allow users to add cards");
        }
    }

    @Override
    public void confirmAddedCards(Session session) {
        if (session.getSessionStatus() == SessionStatus.ADDING_CARDS && session.isParticipantsCanAddCards()) {
            if (session.isCardCommentsAllowed()) {
                session.setSessionStatus(SessionStatus.REVIEWING_CARDS);
            } else {
                session.setSessionStatus(SessionStatus.READY_TO_START);
            }
            Session updatedSession = sessionRepository.save(session);
            if (updatedSession == null) {
                throw new SessionGameServiceException("Session can't be updated");
            }
        } else {
            throw new SessionGameServiceException("Session not in adding cards modus");
        }
    }

    @Override
    public void addComment(User user, CardDetails cardDetails, Comment comment) {
        throw new SessionGameServiceException("Method not implemented");
    }

    @Override
    public void confirmReviews(Session session) {
        if (session.getSessionStatus() == SessionStatus.REVIEWING_CARDS && session.isCardCommentsAllowed()) {
            session.setSessionStatus(SessionStatus.READY_TO_START);
            Session updatedSession = sessionRepository.save(session);
            if (updatedSession == null) {
                throw new SessionGameServiceException("Session can't be updated");
            }
        } else {
            throw new SessionGameServiceException("Session not in reviewing cards modus");
        }
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

    private boolean isUserInSession(Session session, User user) {
        return session.getParticipantInfo().stream().anyMatch(p -> p.getParticipant().getUserId() == user.getUserId());
    }
}
