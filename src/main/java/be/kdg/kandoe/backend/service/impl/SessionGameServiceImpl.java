package be.kdg.kandoe.backend.service.impl;

import be.kdg.kandoe.backend.model.cards.CardDetails;
import be.kdg.kandoe.backend.model.cards.CardPosition;
import be.kdg.kandoe.backend.model.cards.Comment;
import be.kdg.kandoe.backend.model.sessions.CardsChoice;
import be.kdg.kandoe.backend.model.sessions.ParticipantInfo;
import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.backend.model.sessions.SessionStatus;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.CardService;
import be.kdg.kandoe.backend.service.api.EmailService;
import be.kdg.kandoe.backend.service.api.SessionGameService;
import be.kdg.kandoe.backend.service.api.SessionService;
import be.kdg.kandoe.backend.service.exceptions.SessionGameServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class SessionGameServiceImpl implements SessionGameService {
    private final SessionService sessionService;
    private final EmailService emailService;

    @Autowired
    public SessionGameServiceImpl(SessionService sessionService, EmailService emailService) {
        this.sessionService = sessionService;
        this.emailService = emailService;
    }

    @Override
    public void inviteUserForSession(Session session, User user) {
        if (session.getSessionStatus() != SessionStatus.CREATED)
            throw new SessionGameServiceException("Cannot invite user when the session has already started");

        if (session.getParticipantInfo().stream().anyMatch(p -> p.getParticipant().getUserId() == user.getUserId()))
            throw new SessionGameServiceException("User has already been invited to join the session");

        ParticipantInfo participantInfo = new ParticipantInfo();
        participantInfo.setParticipant(user);

        session.getParticipantInfo().add(participantInfo);
        session = sessionService.updateSession(session);
        emailService.sendSessionInvitationToUser(session, session.getOrganizer(), user);
    }

    @Override
    public void confirmInvitedUsers(Session session) {
        if (session.getSessionStatus() != SessionStatus.CREATED)
            throw new SessionGameServiceException("The invited users have already been confirmed for this session");
        
        session.setSessionStatus(SessionStatus.USERS_JOINING);
        sessionService.updateSession(session);
    }

    @Override
    public void setUserJoined(Session session, User user) {
        if (session.getParticipantInfo().size() <= 1)
            throw new SessionGameServiceException("Cannot join a session to which no users are invited");

        if (session.getSessionStatus() == SessionStatus.CREATED)
            throw new SessionGameServiceException("Cannot join a session that has not yet started");
        
        if (session.getSessionStatus() != SessionStatus.USERS_JOINING)
            throw new SessionGameServiceException("Cannot join a session that has already started");
        
        Optional<ParticipantInfo> firstMatchingParticipantInfo = session.getParticipantInfo().stream().filter(p -> p.getParticipant().getUserId() == user.getUserId()).findFirst();

        if (!firstMatchingParticipantInfo.isPresent())
            throw new SessionGameServiceException("User cannot join session to which he is not invited");

        ParticipantInfo participantInfo = firstMatchingParticipantInfo.get();
        participantInfo.setJoined(true);
        participantInfo.setJoinNumber((int) (session.getParticipantInfo().stream().filter(p -> p.isJoined()).count()));

        CardsChoice cardsChoice = new CardsChoice();
        cardsChoice.setParticipant(user);
        cardsChoice.setChosenCards(new ArrayList<>());
        session.getParticipantCardChoices().add(cardsChoice);

        if (session.getParticipantInfo().stream().allMatch(p -> p.isJoined()))
            this.confirmUsersJoined(session);

        session = sessionService.updateSession(session);
    }
    
    @Override
    public void setUserLeft(Session session, User user) {
        if (!isUserInSession(session, user)) {
            throw new SessionGameServiceException("User not in session");
        }

        ParticipantInfo firstMatchingParticipantInfo = session.getParticipantInfo().stream().filter(p -> p.getParticipant().getUserId() == user.getUserId()).findFirst().get();
        firstMatchingParticipantInfo.setJoined(false);
        session = sessionService.updateSession(session);
    }
    
    private void confirmUsersJoined(Session session) {
        if (session.getSessionStatus() != SessionStatus.USERS_JOINING) {
            if (session.getSessionStatus() == SessionStatus.CREATED)
                throw new SessionGameServiceException("Cannot start a session without any invited users");
            else
                throw new SessionGameServiceException("Cannot start an already started session");
        }

        if (!session.getParticipantInfo().stream().allMatch(p -> p.isJoined())) {
            throw new SessionGameServiceException("Cannot start a session when not all users have joined");
        }
        if (session.isParticipantsCanAddCards())
            session.setSessionStatus(SessionStatus.ADDING_CARDS);
        else if (session.isCardCommentsAllowed())
            session.setSessionStatus(SessionStatus.REVIEWING_CARDS);
        else
            session.setSessionStatus(SessionStatus.CHOOSING_CARDS);
    }

    @Override
    public void confirmUserAddedCards(Session session, User user) {
        if (!isUserInSession(session, user))
            throw new SessionGameServiceException("You must be a participant of this session to perform this action");
        
        if (!session.isParticipantsCanAddCards())
            throw new SessionGameServiceException("Adding cards is not allowed for this session");
        
        Optional<ParticipantInfo> optionalParticipantInfo = session.getParticipantInfo()
                .stream().filter(p -> p.getParticipant().getUserId() == user.getUserId()).findFirst();
        
        if (!optionalParticipantInfo.isPresent())
            throw new SessionGameServiceException("Unexpected error: session does not contain any information about the user");
        
        ParticipantInfo participantInfo = optionalParticipantInfo.get();
        
        if (participantInfo.isAddedCardsCompleted())
            throw new SessionGameServiceException("You have already confirmed your added cards");
        
        participantInfo.setAddedCardsCompleted(true);
        
        if (session.getParticipantInfo().stream().allMatch(p -> p.isAddedCardsCompleted())) {
            if (session.isCardCommentsAllowed()) {
                session.setSessionStatus(SessionStatus.REVIEWING_CARDS);
            } else {
                session.setSessionStatus(SessionStatus.CHOOSING_CARDS);
            }
        }

        sessionService.updateSession(session);
    }

    @Override
    public void confirmAddedCards(Session session) {
        if (session.getSessionStatus() == SessionStatus.ADDING_CARDS && session.isParticipantsCanAddCards()) {
            if (session.isCardCommentsAllowed()) {
                session.setSessionStatus(SessionStatus.REVIEWING_CARDS);
            } else {
                session.setSessionStatus(SessionStatus.CHOOSING_CARDS);
            }
            sessionService.updateSession(session);
        } else {
            throw new SessionGameServiceException("Session not in adding cards mode");
        }
    }

    @Override
    public void chooseCards(Session session, User user, Set<CardDetails> cardDetailsToChoose) {
        if (session.getSessionStatus() != SessionStatus.CHOOSING_CARDS)
            throw new SessionGameServiceException("Session must be in state 'choosing cards' to be able to choose cards");
        
        if (!isUserInSession(session, user))
            throw new SessionGameServiceException("You cannot choose cards for a session you are not a participant of");
        
        if (cardDetailsToChoose == null || cardDetailsToChoose.isEmpty())
            throw new SessionGameServiceException("The list of cards to choose must not be empty");
        
        Set<CardDetails> availableCards = (session.getTopic() != null ? session.getTopic().getCards() : session.getCategory().getCards());
        
        if (availableCards == null || availableCards.isEmpty())
            throw new SessionGameServiceException("Cannot choose any cards as there are no cards available for the session");
        
        if (cardDetailsToChoose.size() < session.getMinNumberOfCardsPerParticipant())
            throw new SessionGameServiceException("You are required to select a minimum of " + session.getMinNumberOfCardsPerParticipant() + " cards for this session.");
        
        if (cardDetailsToChoose.size() > session.getMaxNumberOfCardsPerParticipant())
            throw new SessionGameServiceException("You cannot select more than " + session.getMaxNumberOfCardsPerParticipant() + " cards for this session.");
        
        Optional<CardsChoice> optionalCardsChoice = session.getParticipantCardChoices().stream()
                .filter(p -> p.getParticipant().getUserId() == user.getUserId()).findFirst();

        CardsChoice cardsChoice = null;
        
        if (!optionalCardsChoice.isPresent()) {
            cardsChoice = new CardsChoice();
            
            cardsChoice.setParticipant(user);
            cardsChoice.setChosenCards(new ArrayList<>());

            session.getParticipantCardChoices().add(cardsChoice);
        } else {
            cardsChoice = optionalCardsChoice.get();

            if (!cardsChoice.getChosenCards().isEmpty())
                throw new SessionGameServiceException("User has already chosen cards for this session");
        }
        
        for (CardDetails cardDetails : cardDetailsToChoose) {
            if (cardDetails == null)
                throw new SessionGameServiceException("Null is not a valid card to choose");
            
            if (!availableCards.stream().anyMatch(c -> c.getCardDetailsId() == cardDetails.getCardDetailsId()))
                throw new SessionGameServiceException("The chosen card does not exist in the session");
            
            cardsChoice.getChosenCards().add(cardDetails);
        }
        
        sessionService.updateSession(session);
        
        if (session.getParticipantInfo().size() == session.getParticipantCardChoices().size()) {
            boolean allParticipantsHaveChosenCards = true;

            for (CardsChoice choice : session.getParticipantCardChoices()) {
                if (choice.getChosenCards() == null || choice.getChosenCards().isEmpty())
                    allParticipantsHaveChosenCards = false;
            }
            
            if (allParticipantsHaveChosenCards) {
                session.setSessionStatus(SessionStatus.READY_TO_START);
                sessionService.updateSession(session);
            }
        }
    }

    @Override
    public void confirmReviews(Session session, User user) {
        Optional<ParticipantInfo> optional = session.getParticipantInfo().stream().filter(p -> p.getParticipant().getUserId() == user.getUserId()).findFirst();

        if (optional.isPresent()){
            optional.get().setReviewingCardsCompleted(true);
        } else {
            throw new SessionGameServiceException("User not in session");
        }

        if (session.getSessionStatus() == SessionStatus.REVIEWING_CARDS && session.isCardCommentsAllowed()) {
            if (session.getParticipantInfo().stream().allMatch(p -> p.isReviewingCardsCompleted())){
                session.setSessionStatus(SessionStatus.CHOOSING_CARDS);
            }
        } else {
            throw new SessionGameServiceException("Session not in reviewing cards modus");
        }
        sessionService.updateSession(session);
    }

    @Override
    public void startGame(Session session) {
        if (session.getSessionStatus() == SessionStatus.READY_TO_START) {
            session.setSessionStatus(SessionStatus.IN_PROGRESS);
            initCardPositions(session);
            session.setCurrentParticipantPlaying(getNextParticipant(session));
            sessionService.updateSession(session);
        } else {
            throw new SessionGameServiceException("Session isn't ready to start");
        }
    }

    @Override
    public void confirmChoosingCards(Session session) {
        if (session.getSessionStatus() == SessionStatus.CHOOSING_CARDS) {
            session.setSessionStatus(SessionStatus.READY_TO_START);

            sessionService.updateSession(session);
        } else {
            throw new SessionGameServiceException("Session not in choosing cards modus");
        }
    }

    private void initCardPositions(Session session) {
        Set<CardDetails> chosenUniqueCards = session.getParticipantCardChoices().stream().map(p -> p.getChosenCards()).flatMap(c -> c.stream()).distinct().collect(Collectors.toSet());
        session.setCardPositions(chosenUniqueCards.stream().map(c -> new CardPosition(c, session)).collect(Collectors.toList()));
    }

    @Override
    public ParticipantInfo getNextParticipant(Session session) {
        ParticipantInfo currentPlayingParticipant = session.getCurrentParticipantPlaying();
        if (currentPlayingParticipant != null) {
            Optional<ParticipantInfo> maxParticipantInfoOptional = session.getParticipantSequence().stream().max((p1, p2) -> Integer.compare(p1.getJoinNumber(), p2.getJoinNumber()));
            int max;
            if (maxParticipantInfoOptional.isPresent()) {
                max = maxParticipantInfoOptional.get().getJoinNumber();
            } else {
                max = 0;
            }
            if (currentPlayingParticipant.getJoinNumber() == max) {

                return session.getParticipantSequence().get(0);

            } else {
                int currentIndex = 0;
                List<ParticipantInfo> participantSequence = session.getParticipantSequence();

                for (int i = 0; i < participantSequence.size(); i++) {
                    if (participantSequence.get(i).getJoinNumber() == currentPlayingParticipant.getJoinNumber())
                        currentIndex = i;
                }

                return participantSequence.get(currentIndex + 1);
            }
        } else {
            return session.getParticipantSequence().get(0);
        }
    }

    @Override
    public CardPosition increaseCardPriority(Session session, User user, CardDetails cardDetails) {
        if (session.getSessionStatus() != SessionStatus.IN_PROGRESS) {
            throw new SessionGameServiceException("Game isn't in progress");
        }

        if (!isUserInSession(session, user)) {
            throw new SessionGameServiceException("User is not in session");
        }
        if (session.getCurrentParticipantPlaying().getParticipant().getUserId() != user.getUserId()) {
            throw new SessionGameServiceException("Not the turn of this user");
        }

        if(!session.getCardPositions().stream().anyMatch(c -> c.getCardDetails().getCardDetailsId() == cardDetails.getCardDetailsId())){
            throw new SessionGameServiceException("Card is not in game");
        }

        Optional<CardPosition> cardPositionOptional = session.getCardPositions().stream().filter(c -> c.getCardDetails().getCardDetailsId() == cardDetails.getCardDetailsId()).findFirst();
        if (cardPositionOptional.isPresent()) {

            CardPosition cardPosition = cardPositionOptional.get();
            cardPosition.setPriority(cardPosition.getPriority() + 1);

            if (anyWinners(session)) {
                endGame(session);
            }

            session.setCurrentParticipantPlaying(getNextParticipant(session));
            sessionService.updateSession(session);
            
            return cardPosition;
        } else {
            throw new SessionGameServiceException("Card is not on the board");
        }
    }

    private boolean anyWinners(Session session) {
        return session.getCardPositions().stream().anyMatch(c -> c.getPriority() == session.getAmountOfCircles());
    }

    @Override
    public void endGame(Session session) {
        if (session.getSessionStatus() == SessionStatus.IN_PROGRESS) {
            List<CardPosition> cardPositions = session.getCardPositions();
            int highestPriority = cardPositions.stream().mapToInt(c -> c.getPriority()).max().getAsInt();
            List<CardDetails> winners = session.getCardPositions().stream().filter(c -> c.getPriority() == highestPriority).map(CardPosition::getCardDetails).collect(Collectors.toList());
            session.setWinners(winners);
            session.setSessionStatus(SessionStatus.FINISHED);
            sessionService.updateSession(session);
        } else {
            throw new SessionGameServiceException("Game isn't in progress. Game can't be stopped");
        }
    }

    private boolean isUserInSession(Session session, User user) {
        return session.getParticipantInfo().stream().anyMatch(p -> p.getParticipant().getUserId() == user.getUserId());
    }
}
