package be.kdg.kandoe.backend.service.impl;

import be.kdg.kandoe.backend.model.cards.CardDetails;
import be.kdg.kandoe.backend.model.cards.CardPosition;
import be.kdg.kandoe.backend.model.cards.Comment;
import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Topic;
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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SessionGameServiceImpl implements SessionGameService {
    private final CardService cardService;
    private final SessionService sessionService;
    private final EmailService emailService;

    @Autowired
    public SessionGameServiceImpl(SessionService sessionService, EmailService emailService, CardService cardService) {
        this.sessionService = sessionService;
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
        session.getParticipantSequence().add(participantInfo);
        session = sessionService.updateSession(session);
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
        //TODO controle op aantal spelers
        if (session.getParticipantInfo().size() <= 1)
            throw new SessionGameServiceException("Cannot join a session to which no users are invited");

        Optional<ParticipantInfo> firstMatchingParticipantInfo = session.getParticipantInfo().stream().filter(p -> p.getParticipant().getUserId() == user.getUserId()).findFirst();

        if (!firstMatchingParticipantInfo.isPresent())
            throw new SessionGameServiceException("User cannot join session to which he is not invited");

        ParticipantInfo participantInfo = firstMatchingParticipantInfo.get();
        participantInfo.setJoined(true);
        participantInfo.setJoinNumber(session.getParticipantInfo().size() + 1);

        CardsChoice cardsChoice = new CardsChoice();
        cardsChoice.setParticipant(user);
        cardsChoice.setChosenCards(new ArrayList<>());
        session.getParticipantCardChoices().add(cardsChoice);

        session = sessionService.updateSession(session);

        if (session.getParticipantInfo().stream().allMatch(p -> p.isJoined()))
            this.confirmUsersJoined(session);
    }

    //TODO: testen
    @Override
    public void setUserLeft(Session session, User user) {
        if (isUserInSession(session, user)) {
            ParticipantInfo firstMatchingParticipantInfo = session.getParticipantInfo().stream().filter(p -> p.getParticipant().getUserId() == user.getUserId()).findFirst().get();
            firstMatchingParticipantInfo.setJoined(false);
            session = sessionService.updateSession(session);
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
                session.setSessionStatus(SessionStatus.CHOOSING_CARDS);
            }
            sessionService.updateSession(session);
        } else {
            throw new SessionGameServiceException("Session not in adding cards modus");
        }
    }

    @Override
    public void chooseCards(Session session, User user, CardDetails cardDetails) {
        if (session.getSessionStatus() == SessionStatus.CHOOSING_CARDS) {
            if (isUserInSession(session, user)) {
                Set<CardDetails> availableCards = new HashSet<>();
                if (session.getTopic() != null) {
                    availableCards = session.getTopic().getCards();
                } else {
                    availableCards = session.getCategory().getCards();
                }
                if (availableCards.stream().anyMatch(c -> c.getCardDetailsId() == cardDetails.getCardDetailsId())) {
                    Optional<CardsChoice> cardsChoiceOptional = session.getParticipantCardChoices().stream().filter(p -> p.getParticipant().getUserId() == user.getUserId()).findFirst();
                    CardsChoice cardsChoice;
                    if (cardsChoiceOptional.isPresent()) {
                        cardsChoice = cardsChoiceOptional.get();
                    } else {
                        throw new SessionGameServiceException("User has no CardChoice. This shouldn't happen");
                    }
                    if (session.getMaxNumberOfCardsPerParticipant() > cardsChoice.getChosenCards().size()) {
                        if (!cardsChoice.getChosenCards().stream().anyMatch(c -> c.getCardDetailsId() == cardDetails.getCardDetailsId())) {
                            cardsChoice.getChosenCards().add(cardDetails);

                            sessionService.updateSession(session);
                        } else {
                            //TODO is het wel nodig op een exception op te gooien?
                        }
                    } else {
                        throw new SessionGameServiceException(String.format("User has exceeded the max threshold of cards (%d)", session.getMaxNumberOfCardsPerParticipant()));
                    }

                } else {
                    throw new SessionGameServiceException("Card doesn't exist in topic/category");
                }
            } else {
                throw new SessionGameServiceException("User is not in session");
            }
        } else {
            throw new SessionGameServiceException("Session not in choosing card modus");
        }
    }

    @Override
    public void confirmCardsChosen(Session session, User user) {
        if (session.getSessionStatus() == SessionStatus.CHOOSING_CARDS) {
            if (isUserInSession(session, user)) {
                Optional<CardsChoice> cardChoiceOptional = session.getParticipantCardChoices().stream().filter(p -> p.getParticipant().getUserId() == user.getUserId()).findFirst();
                if (cardChoiceOptional.isPresent()) {
                    CardsChoice cardsChoice = cardChoiceOptional.get();
                    if (cardsChoice.getChosenCards().size() >= session.getMinNumberOfCardsPerParticipant() && cardsChoice.getChosenCards().size() <= session.getMaxNumberOfCardsPerParticipant()) {
                        cardsChoice.setCardsChosen(true);
                    } else {
                        throw new SessionGameServiceException("User has more/less cards than allowed");
                    }
                } else {
                    throw new SessionGameServiceException("User didn't choose any cards yet.");
                }

                if (session.getParticipantCardChoices().stream().allMatch(p -> p.isCardsChosen())) {
                    confirmChoosingCards(session);
                }
            } else {
                throw new SessionGameServiceException("User not in session");
            }
        } else {
            throw new SessionGameServiceException("Session not in choosing cards modus");
        }
    }

    @Override
    public void addReview(User user, CardDetails cardDetails, Comment comment) {
        throw new SessionGameServiceException("Method not implemented");
    }

    @Override
    public void confirmReviews(Session session) {
        if (session.getSessionStatus() == SessionStatus.REVIEWING_CARDS && session.isCardCommentsAllowed()) {
            session.setSessionStatus(SessionStatus.CHOOSING_CARDS);
            sessionService.updateSession(session);
        } else {
            throw new SessionGameServiceException("Session not in reviewing cards modus");
        }
    }

    @Override
    public void startGame(Session session) {
        if (session.getSessionStatus() == SessionStatus.READY_TO_START) {
            session.setSessionStatus(SessionStatus.IN_PROGRESS);
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
            session.setCurrentParticipantPlaying(getNextParticipant(session));

            initCardPositions(session);

            sessionService.updateSession(session);
        } else {
            throw new SessionGameServiceException("Session not in choosing cards modus");
        }
    }

    private void initCardPositions(Session session) {
        Set<CardDetails> chosenUniqueCards = session.getParticipantCardChoices().stream().map(p -> p.getChosenCards()).flatMap(c -> c.stream()).distinct().collect(Collectors.toSet());
        session.setCardPositions(chosenUniqueCards.stream().map(c -> new CardPosition(c, session)).collect(Collectors.toList()));
    }

    private ParticipantInfo getNextParticipant(Session session) {
        /*
        ParticipantInfo currentPlayingParticipant = session.getCurrentParticipantPlaying();
        if (currentPlayingParticipant != null) {
            int max = session.getParticipantSequence().stream().max((p1, p2) -> Integer.compare(p1.getJoinNumber(), p2.getJoinNumber())).get().getJoinNumber();
            if (currentPlayingParticipant.getJoinNumber() == max){
                return session.getParticipantSequence().stream()
                        .sorted((p1, p2) -> Integer.compare(p1.getJoinNumber(), p2.getJoinNumber()))
                        .findFirst().get();
            } else {
                return session.getParticipantSequence().stream()
                        .sorted((p1, p2) -> Integer.compare(p1.getJoinNumber(), p2.getJoinNumber()))
                        .filter(p -> p.getJoinNumber() >= currentPlayingParticipant.getJoinNumber() + 1)
                        .findFirst().get();
            }
        } else {
            throw new SessionGameServiceException("No next particapent found");
        }
        */
    }

    @Override
    public void increaseCardPriority(Session session, User user, CardDetails cardDetails) {
        if (session.getSessionStatus() == SessionStatus.IN_PROGRESS) {
            if (isUserInSession(session, user)) {
                if (session.getCurrentParticipantPlaying().getParticipant().getUserId() == user.getUserId()) {
                    Optional<ParticipantInfo> participantInfoOptional = session.getParticipantInfo().stream().filter(p -> p.getParticipant().getUserId() == user.getUserId()).findFirst();
                    Optional<CardsChoice> cardsChoiceOptional = session.getParticipantCardChoices().stream().filter(p -> p.getParticipant().getUserId() == user.getUserId()).findFirst();
                    if (participantInfoOptional.isPresent() && cardsChoiceOptional.isPresent()) {
                        ParticipantInfo participantInfo = participantInfoOptional.get();
                        CardsChoice cardsChoice = cardsChoiceOptional.get();
                        if (!participantInfo.isMadeMove()) {
                            Optional<CardPosition> cardPositionOptional = session.getCardPositions().stream().filter(c -> c.getCardDetails().getCardDetailsId() == c.getCardDetails().getCardDetailsId()).findFirst();
                            if (cardsChoiceOptional.isPresent()) {
                                CardPosition cardPosition = cardPositionOptional.get();
                                cardPosition.setPriority(cardPosition.getPriority() + 1);
                                participantInfo.setMadeMove(true);
                                session.setCurrentParticipantPlaying(getNextParticipant(session));
                                if (session.getParticipantInfo().stream().allMatch(p -> p.isMadeMove())) {
                                    session.getParticipantInfo().forEach(p -> p.setMadeMove(false));
                                }
                                sessionService.updateSession(session);
                            } else {
                                throw new SessionGameServiceException("Card is not in game");
                            }
                        } else {
                            throw new SessionGameServiceException("User already made a move this round");
                        }
                    }
                } else {
                    throw new SessionGameServiceException("Not the turn of this user");
                }
            } else {
                throw new SessionGameServiceException("User is not in session");
            }
        } else {
            throw new SessionGameServiceException("Game isn't in progress");
        }
    }

    @Override
    public void endGame(Session session) {
        throw new SessionGameServiceException("Method not implemented");
    }

    private boolean isUserInSession(Session session, User user) {
        return session.getParticipantInfo().stream().anyMatch(p -> p.getParticipant().getUserId() == user.getUserId());
    }
}
