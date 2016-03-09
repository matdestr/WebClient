package be.kdg.kandoe.frontend.controller.rest;

import be.kdg.kandoe.backend.model.cards.CardDetails;
import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.*;
import be.kdg.kandoe.frontend.controller.resources.cards.CardDetailsResource;
import be.kdg.kandoe.frontend.controller.resources.cards.CreateCardDetailsResource;
import be.kdg.kandoe.frontend.controller.rest.exceptions.CanDoControllerRuntimeException;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/sessions")
@PreAuthorize("isAuthenticated()")
public class SessionGameRestController {
    // TODO : GET   /{sessionId}/all-cards
    // TODO : POST  /{sessionId}/all-cards
    // TODO : GET   /{sessionId}/chosen-cards
    // TODO : POST  /{sessionId}/chosen-cards
    // TODO : GET   /{sessionId}/reviews
    // TODO : POST  /{sessionId}/reviews
    // TODO : GET   /{sessionId}/positions
    // TODO : PUT   /{sessionId}/positions
    
    private UserService userService;
    private SessionService sessionService;
    private SessionGameService sessionGameService;
    private CardService cardService;
    
    private MapperFacade mapperFacade;

    @Autowired
    public SessionGameRestController(UserService userService, SessionService sessionService,
                                     SessionGameService sessionGameService, CardService cardService, 
                                     MapperFacade mapperFacade) {
        this.userService = userService;
        this.sessionService = sessionService;
        this.sessionGameService = sessionGameService;
        this.cardService = cardService;
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
    
    @RequestMapping(value = "/{sessionId}/invite", method = RequestMethod.POST)
    public ResponseEntity inviteUser(@AuthenticationPrincipal User user,
                                     @PathVariable("sessionId") int sessionId,
                                     @RequestParam("userId") int userId) {
        Session session = sessionService.getSessionById(sessionId);
        User userToInvite = userService.getUserByUserId(userId);

        /*if (!session.getCategory().getOrganization().isOrganizer(user))
            return new ResponseEntity(HttpStatus.FORBIDDEN);*/
        checkUserIsParticipant(user, session);

        sessionGameService.inviteUserForSession(session, userToInvite);

        return new ResponseEntity(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{sessionId}/join", method = RequestMethod.POST)
    public ResponseEntity joinSession(@AuthenticationPrincipal User user,
                                      @PathVariable("sessionId") int sessionId) {
        Session session = sessionService.getSessionById(sessionId);

        /*if (!session.isUserParticipant(user.getUserId()))
            return new ResponseEntity(HttpStatus.FORBIDDEN);*/
        checkUserIsParticipant(user, session);

        sessionGameService.setUserJoined(session, user);

        return new ResponseEntity(HttpStatus.CREATED);
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
    
    @RequestMapping(value = "/{sessionId}/all-cards", method = RequestMethod.POST)
    public ResponseEntity<CardDetailsResource> addCardToSession(@AuthenticationPrincipal User user,
                                                                @PathVariable("sessionId") int sessionId,
                                                                @Valid @RequestBody CreateCardDetailsResource resource) {
        Session session = sessionService.getSessionById(sessionId);
        checkUserIsParticipant(user, session);
        
        if (!session.isParticipantsCanAddCards())
            throw new CanDoControllerRuntimeException("Participants are not allowed to add cards for this session", HttpStatus.FORBIDDEN);
        
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
    
    /*@RequestMapping(value = "/{sessionId}/chosen-cards", method = RequestMethod.GET)
    public ResponseEntity<List<CardDetailsResource>> getChosenCards(@AuthenticationPrincipal User user,
                                                                    @PathVariable("sessionId") int sessionId) {
        Session session = sessionService.getSessionById(sessionId);
        
        if (session == null)
            throw new CanDoControllerRuntimeException("Could not find session with ID " + sessionId, HttpStatus.NOT_FOUND);
        
        // TODO
    }
    
    @RequestMapping(value = "/{sessionId}/chosen-cards", method = RequestMethod.POST)
    public ResponseEntity chooseCards(@AuthenticationPrincipal User user,
                                      @PathVariable("sessionId") int sessionId) {
        
    }*/
}
