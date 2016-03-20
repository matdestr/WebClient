package be.kdg.kandoe.frontend.controller.rest;

import be.kdg.kandoe.backend.model.cards.CardDetails;
import be.kdg.kandoe.backend.model.cards.CardPosition;
import be.kdg.kandoe.backend.model.cards.Comment;
import be.kdg.kandoe.backend.model.sessions.CardsChoice;
import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.backend.model.sessions.SessionStatus;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.CardService;
import be.kdg.kandoe.backend.service.api.SessionGameService;
import be.kdg.kandoe.backend.service.api.SessionService;
import be.kdg.kandoe.backend.service.api.UserService;
import be.kdg.kandoe.frontend.controller.resources.cards.CardDetailsResource;
import be.kdg.kandoe.frontend.controller.resources.cards.CommentResource;
import be.kdg.kandoe.frontend.controller.resources.cards.CreateCardDetailsResource;
import be.kdg.kandoe.frontend.controller.resources.organizations.EmailResource;
import be.kdg.kandoe.frontend.controller.resources.sessions.CardPositionResource;
import be.kdg.kandoe.frontend.controller.resources.sessions.chat.ChatMessageResource;
import be.kdg.kandoe.frontend.controller.resources.sessions.reviews.CreateCardReviewOverview;
import be.kdg.kandoe.frontend.controller.resources.users.UserResource;
import be.kdg.kandoe.frontend.controller.rest.exceptions.CanDoControllerRuntimeException;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/sessions")
@PreAuthorize("isAuthenticated()")
public class SessionGameRestController {
    private UserService userService;
    private SessionService sessionService;
    private SessionGameService sessionGameService;
    private CardService cardService;

    private SimpMessagingTemplate simpMessagingTemplate;
    private MapperFacade mapperFacade;

    @Autowired
    public SessionGameRestController(UserService userService, SessionService sessionService,
                                     SessionGameService sessionGameService, CardService cardService,
                                     SimpMessagingTemplate simpMessagingTemplate, MapperFacade mapperFacade) {
        this.userService = userService;
        this.sessionService = sessionService;
        this.sessionGameService = sessionGameService;
        this.cardService = cardService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.mapperFacade = mapperFacade;
    }

    private void checkUserIsParticipant(User user, Session session) {
        if (session == null)
            throw new CanDoControllerRuntimeException("Invalid session ID");

        if (user == null)
            throw new CanDoControllerRuntimeException("Invalid user ID");

        if (!session.isUserParticipant(user.getUserId()))
            throw new CanDoControllerRuntimeException("You must be a participant of the session to perform this action", HttpStatus.FORBIDDEN);
    }

    private void checkUserIsOrganizer(User user, Session session) {
        checkUserIsParticipant(user, session);

        if (!session.getOrganizer().equals(user))
            throw new CanDoControllerRuntimeException("You must be the organizer of the session to perform this action", HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/{sessionId}/invite", method = RequestMethod.POST)
    public ResponseEntity inviteUser(@AuthenticationPrincipal User user,
                                     @PathVariable("sessionId") int sessionId,
                                     @RequestParam("email") String email) {
        Session session = sessionService.getSessionById(sessionId);

        User userToInvite = userService.getUserByEmail(email);

        if (userToInvite == null) {
            //todo user inviten
            //sessionGameService.inviteNonExisitingUserForSession();
            return new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);
        } else {
            checkUserIsOrganizer(user, session);
            sessionGameService.inviteUserForSession(session, userToInvite);
            return new ResponseEntity(HttpStatus.CREATED);
        }
    }

    @RequestMapping(value = "/{sessionId}/invite-all", method = RequestMethod.POST)
    public ResponseEntity inviteUsers(@AuthenticationPrincipal User user,
                                      @PathVariable("sessionId") int sessionId,
                                      @RequestBody List<EmailResource> emails) {
        Session session = sessionService.getSessionById(sessionId);

        for (EmailResource email : emails) {
            User userToInvite = userService.getUserByEmail(email.getEmail());
            checkUserIsOrganizer(user, session);
            sessionGameService.inviteUserForSession(session, userToInvite);
        }

        return new ResponseEntity(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{sessionId}/invite/confirm", method = RequestMethod.POST)
    public ResponseEntity inviteUser(@AuthenticationPrincipal User user,
                                     @PathVariable("sessionId") int sessionId) {
        Session session = sessionService.getSessionById(sessionId);

        checkUserIsOrganizer(user, session);
        sessionGameService.confirmInvitedUsers(session);
        this.sendSessionStatusUpdate(sessionId, session.getSessionStatus());

        return new ResponseEntity(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{sessionId}/join", method = RequestMethod.POST)
    public ResponseEntity joinSession(@AuthenticationPrincipal User user,
                                      @PathVariable("sessionId") int sessionId) {
        Session session = sessionService.getSessionById(sessionId);

        checkUserIsParticipant(user, session);

        sessionGameService.setUserJoined(session, user);
        this.sendSessionParticipantJoined(sessionId, this.mapperFacade.map(user, UserResource.class));

        if (session.getSessionStatus() != SessionStatus.USERS_JOINING)
            this.sendSessionStatusUpdate(sessionId, session.getSessionStatus());

        return new ResponseEntity(HttpStatus.CREATED);
    }

    //Todo reset status
    @RequestMapping(value = "/{sessionId}/decline", method = RequestMethod.POST)
    public ResponseEntity declineSession(@AuthenticationPrincipal User user,
                                         @PathVariable("sessionId") int sessionId) {
        Session session = sessionService.getSessionById(sessionId);

        checkUserIsParticipant(user, session);

        sessionGameService.setUserLeft(session, user);

        if (session.getSessionStatus() != SessionStatus.USERS_JOINING)
            this.sendSessionStatusUpdate(sessionId, session.getSessionStatus());

        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/{sessionId}/all-cards", method = RequestMethod.GET)
    public ResponseEntity<List<CardDetailsResource>> getCardDetailsOfSession(@AuthenticationPrincipal User user,
                                                                             @PathVariable("sessionId") int sessionId) {
        Session session = sessionService.getSessionById(sessionId);
        checkUserIsParticipant(user, session);

        Set<CardDetails> sessionCardDetails;

        if (session.getTopic() == null) {
            sessionCardDetails = session.getCategory().getCards();
        } else {
            sessionCardDetails = session.getTopic().getCards();
        }

        List<CardDetailsResource> resources = mapperFacade.mapAsList(sessionCardDetails, CardDetailsResource.class);

        return new ResponseEntity<List<CardDetailsResource>>(resources, HttpStatus.OK);
    }

    @RequestMapping(value = "/{sessionId}/can-add-cards", method = RequestMethod.GET)
    public ResponseEntity canUserStillAddCards(@AuthenticationPrincipal User user,
                                               @PathVariable("sessionId") int sessionId){
        Session session = sessionService.getSessionById(sessionId);
        checkUserHasAlreadyAddedCards(user, session);

        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/{sessionId}/all-cards/addall")
    public ResponseEntity addCardsToSession(@AuthenticationPrincipal User user,
                                            @PathVariable("sessionId") int sessionId,
                                            @Valid @RequestBody List<CreateCardDetailsResource> resources) {
        Session session = sessionService.getSessionById(sessionId);
        checkUserIsParticipant(user, session);
        checkUserHasAlreadyAddedCards(user, session);

        if (!session.isParticipantsCanAddCards())
            throw new CanDoControllerRuntimeException("Participants are not allowed to add cards for this session", HttpStatus.FORBIDDEN);

        if (session.getSessionStatus() != SessionStatus.ADDING_CARDS)
            throw new CanDoControllerRuntimeException("The session is not in the 'adding cards' mode");

        List<CardDetails> allCardDetails = mapperFacade.mapAsList(resources, CardDetails.class);
        allCardDetails.stream().forEach(c -> c.setCreator(user));

        if (session.getTopic() != null)
            cardService.addAllCardDetailsToTopic(session.getTopic(), allCardDetails);
        else
            cardService.addAllCardDetailsToCategory(session.getCategory(), allCardDetails);

        return new ResponseEntity(HttpStatus.OK);
    }

    private void checkUserHasAlreadyAddedCards(User user, Session session) {
        List<CardsChoice> cardsChoices = session.getParticipantCardChoices();
        int userIndex = -1;

        for (int i = 0; i < cardsChoices.size(); i++) {
            CardsChoice cardsChoice = cardsChoices.get(i);
            if (cardsChoice.getParticipant().getUserId() == user.getUserId()) {
                userIndex = i;
                break;
            }
        }

        if (userIndex > -1) {
            List<CardDetails> cardsChosenByUser = cardsChoices.get(userIndex).getChosenCards();

            if (!cardsChosenByUser.isEmpty())
                throw new CanDoControllerRuntimeException("User has already added cards to this session", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/{sessionId}/all-cards", method = RequestMethod.POST)
    public ResponseEntity<CardDetailsResource> addCardToSession(@AuthenticationPrincipal User user,
                                                                @PathVariable("sessionId") int sessionId,
                                                                @Valid @RequestBody CreateCardDetailsResource resource) {
        Session session = sessionService.getSessionById(sessionId);
        checkUserIsParticipant(user, session);

        if (!session.isParticipantsCanAddCards())
            throw new CanDoControllerRuntimeException("Participants are not allowed to add cards for this session", HttpStatus.FORBIDDEN);

        if (session.getSessionStatus() != SessionStatus.ADDING_CARDS)
            throw new CanDoControllerRuntimeException("The session is not in the 'adding cards' mode");

        CardDetails cardDetails = mapperFacade.map(resource, CardDetails.class);
        cardDetails.setCreator(user);

        CardDetails resultingCardDetails;
        CardDetailsResource resultingResource;

        if (session.getTopic() != null) {
            resultingCardDetails = cardService.addCardDetailsToTopic(session.getTopic(), cardDetails);
        } else {
            resultingCardDetails = cardService.addCardDetailsToCategory(session.getCategory(), cardDetails);
        }

        resultingResource = mapperFacade.map(resultingCardDetails, CardDetailsResource.class);

        return new ResponseEntity<>(resultingResource, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{sessionId}/all-cards/confirm", method = RequestMethod.POST)
    public ResponseEntity confirmAddedCards(@AuthenticationPrincipal User user,
                                            @PathVariable("sessionId") int sessionId) {
        Session session = sessionService.getSessionById(sessionId);
        checkUserIsParticipant(user, session);

        if (!session.isParticipantsCanAddCards())
            throw new CanDoControllerRuntimeException("Participants are not allowed to add cards for this session", HttpStatus.FORBIDDEN);

        sessionGameService.confirmUserAddedCards(session, user);

        if (session.getSessionStatus() != SessionStatus.ADDING_CARDS)
            this.sendSessionStatusUpdate(sessionId, session.getSessionStatus());

        return new ResponseEntity(HttpStatus.CREATED);
    }
    
    /*@RequestMapping(value = "/{sessionId}/chosen-cards", method = RequestMethod.GET)
    public ResponseEntity<List<CardDetailsResource>> getChosenCards(@AuthenticationPrincipal User user,
                                                                    @PathVariable("sessionId") int sessionId) {
        Session session = sessionService.getSessionById(sessionId);
        
        if (session == null)
            throw new CanDoControllerRuntimeException("Could not find session with ID " + sessionId, HttpStatus.NOT_FOUND);
        
        List<CardsChoice> cardChoices = session.getParticipantCardChoices();
    }*/

    @RequestMapping(value = "/{sessionId}/chosen-cards", method = RequestMethod.POST)
    public ResponseEntity chooseCards(@AuthenticationPrincipal User user,
                                      @PathVariable("sessionId") int sessionId,
                                      @RequestParam("cardDetailsId") List<Integer> cardDetailsIds) {
        Session session = sessionService.getSessionById(sessionId);
        checkUserIsParticipant(user, session);
        Set<CardDetails> cardDetails = new HashSet<>();

        for (int cardDetailsId : cardDetailsIds) {
            CardDetails c = cardService.getCardDetailsById(cardDetailsId);
            cardDetails.add(c);
        }

        sessionGameService.chooseCards(session, user, cardDetails);

        if (session.getSessionStatus() != SessionStatus.CHOOSING_CARDS)
            this.sendSessionStatusUpdate(sessionId, session.getSessionStatus());

        return new ResponseEntity(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{sessionId}/start", method = RequestMethod.POST)
    public ResponseEntity startGame(@AuthenticationPrincipal User user,
                                    @PathVariable("sessionId") int sessionId) {
        Session session = sessionService.getSessionById(sessionId);
        checkUserIsOrganizer(user, session);
        sessionGameService.startGame(session);

        UserResource currentParticipantResource =
                mapperFacade.map(session.getCurrentParticipantPlaying().getParticipant(), UserResource.class);

        this.sendSessionStatusUpdate(sessionId, session.getSessionStatus());
        this.sendSessionCurrentParticipantUpdate(sessionId, currentParticipantResource);

        return new ResponseEntity(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{sessionId}/positions", method = RequestMethod.GET)
    public ResponseEntity<List<CardPositionResource>> getCardPositions(@AuthenticationPrincipal User user,
                                                                       @PathVariable("sessionId") int sessionId) {
        Session session = sessionService.getSessionById(sessionId);

        if (session == null)
            throw new CanDoControllerRuntimeException("Could not find session with ID " + sessionId, HttpStatus.NOT_FOUND);

        this.checkUserIsParticipant(user, session);

        List<CardPosition> cardPositions = session.getCardPositions();
        List<CardPositionResource> cardPositionResources = mapperFacade.mapAsList(cardPositions, CardPositionResource.class);

        return new ResponseEntity<>(cardPositionResources, HttpStatus.OK);
    }

    @RequestMapping(value = "/{sessionId}/reviews", method = RequestMethod.POST)
    public ResponseEntity addReview(@AuthenticationPrincipal User user,
                                    @PathVariable("sessionId") int sessionId,
                                    @Valid @RequestBody CreateCardReviewOverview createCardReviewOverview) {
        Session session = sessionService.getSessionById(sessionId);

        if (session == null)
            throw new CanDoControllerRuntimeException("Could not find session with ID " + sessionId, HttpStatus.NOT_FOUND);

        if (session.getSessionStatus() != SessionStatus.REVIEWING_CARDS || !session.isCardCommentsAllowed()) {
            throw new CanDoControllerRuntimeException("Session is not in review modus or comments allowed");
        }

        this.checkUserIsParticipant(user, session);

        CardDetails cardDetails = cardService.getCardDetailsById(createCardReviewOverview.getCardDetailsId());

        if (cardDetails == null)
            throw new CanDoControllerRuntimeException("Could not find carddetails with ID " + createCardReviewOverview.getCardDetailsId(), HttpStatus.NOT_FOUND);

        Comment comment = cardService.addReview(user, cardDetails, createCardReviewOverview.getMessage());
        CommentResource commentResource = mapperFacade.map(comment, CommentResource.class);

        return new ResponseEntity<>(commentResource, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{sessionId}/reviews/confirm", method = RequestMethod.POST)
    public ResponseEntity confirmReviews(@AuthenticationPrincipal User user, @PathVariable("sessionId") int sessionId) {
        Session session = sessionService.getSessionById(sessionId);

        if (session == null)
            throw new CanDoControllerRuntimeException("Could not find session with ID " + sessionId, HttpStatus.NOT_FOUND);

        if (session.getSessionStatus() != SessionStatus.REVIEWING_CARDS || !session.isCardCommentsAllowed()) {
            throw new CanDoControllerRuntimeException("Session is not in review modus or comments allowed");
        }

        this.checkUserIsParticipant(user, session);
        sessionGameService.confirmReviews(session, user);

        if (session.getSessionStatus() != SessionStatus.REVIEWING_CARDS)
            this.sendSessionStatusUpdate(sessionId, session.getSessionStatus());

        return new ResponseEntity(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{sessionId}/positions", method = RequestMethod.PUT)
    public ResponseEntity increaseCardPriority(@AuthenticationPrincipal User user,
                                               @PathVariable("sessionId") int sessionId,
                                               @RequestParam("cardDetailsId") int cardDetailsId) {
        Session session = sessionService.getSessionById(sessionId);
        CardDetails cardDetails = cardService.getCardDetailsById(cardDetailsId);

        if (session == null)
            throw new CanDoControllerRuntimeException("Could not find session with ID " + sessionId, HttpStatus.NOT_FOUND);

        CardPosition cardPosition = sessionGameService.increaseCardPriority(session, user, cardDetails);
        CardPositionResource cardPositionResource = mapperFacade.map(cardPosition, CardPositionResource.class);

        UserResource currentParticipantResource =
                mapperFacade.map(session.getCurrentParticipantPlaying().getParticipant(), UserResource.class);

        this.sendSessionCardPositionUpdate(sessionId, cardPositionResource);
        this.sendSessionCurrentParticipantUpdate(sessionId, currentParticipantResource);

        if (session.getSessionStatus() != SessionStatus.IN_PROGRESS)
            this.sendSessionStatusUpdate(sessionId, session.getSessionStatus());

        return new ResponseEntity<>(cardPositionResource, HttpStatus.OK);
    }

    @RequestMapping(value = "/{sessionId}/chat", method = RequestMethod.GET)
    public ResponseEntity<List<ChatMessageResource>> getChatMessagesOfSession(@AuthenticationPrincipal User user,
                                                                              @PathVariable("sessionId") int sessionId) {
        Session session = sessionService.getSessionById(sessionId);

        if (session == null)
            throw new CanDoControllerRuntimeException("Could not find session with ID " + sessionId, HttpStatus.NOT_FOUND);

        checkUserIsParticipant(user, session);
        List<ChatMessageResource> chatMessageResources = mapperFacade.mapAsList(session.getChatMessages(), ChatMessageResource.class);

        return new ResponseEntity<>(chatMessageResources, HttpStatus.OK);
    }

    @RequestMapping(value = "/{sessionId}/end", method = RequestMethod.POST)
    public ResponseEntity endGame(@AuthenticationPrincipal User user,
                                  @PathVariable("sessionId") int sessionId) {
        Session session = sessionService.getSessionById(sessionId);

        if (session == null)
            throw new CanDoControllerRuntimeException("Could not find session with ID " + sessionId, HttpStatus.NOT_FOUND);

        checkUserIsOrganizer(user, session);
        sessionGameService.endGame(session);

        this.sendSessionStatusUpdate(sessionId, session.getSessionStatus());

        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/{sessionId}/winning-cards", method = RequestMethod.GET)
    public ResponseEntity<List<CardDetailsResource>> getWinningCards(@AuthenticationPrincipal User user,
                                                                     @PathVariable("sessionId") int sessionId) {
        Session session = sessionService.getSessionById(sessionId);

        if (session == null)
            throw new CanDoControllerRuntimeException("Could not find session with ID " + sessionId, HttpStatus.NOT_FOUND);

        checkUserIsParticipant(user, session);
        List<CardDetailsResource> cardDetailsResources = mapperFacade.mapAsList(session.getWinners(), CardDetailsResource.class);

        return new ResponseEntity<>(cardDetailsResources, HttpStatus.OK);
    }

    private void sendSessionCardPositionUpdate(int sessionId, CardPositionResource cardPositionResource) {
        this.simpMessagingTemplate.convertAndSend(
                "/topic/sessions/" + sessionId + "/positions", cardPositionResource
        );
    }

    private void sendSessionParticipantJoined(int sessionId, UserResource participant) {
        this.simpMessagingTemplate.convertAndSend(
                "/topic/sessions/" + sessionId + "/participants", participant
        );
    }

    private void sendSessionCurrentParticipantUpdate(int sessionId, UserResource participant) {
        this.simpMessagingTemplate.convertAndSend(
                "/topic/sessions/" + sessionId + "/current-participant", participant
        );
    }

    private void sendSessionStatusUpdate(int sessionId, SessionStatus sessionStatus) {
        this.simpMessagingTemplate.convertAndSend(
                "/topic/sessions/" + sessionId + "/status", sessionStatus
        );
    }
}
