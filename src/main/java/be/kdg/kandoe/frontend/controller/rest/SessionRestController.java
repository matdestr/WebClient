package be.kdg.kandoe.frontend.controller.rest;

import be.kdg.kandoe.backend.model.cards.CardDetails;
import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.organizations.Topic;
import be.kdg.kandoe.backend.model.sessions.AsynchronousSession;
import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.backend.model.sessions.SynchronousSession;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.*;
import be.kdg.kandoe.frontend.controller.resources.cards.CreateCardDetailsResource;
import be.kdg.kandoe.frontend.controller.resources.organizations.categories.CategoryResource;
import be.kdg.kandoe.frontend.controller.resources.sessions.SessionResource;
import be.kdg.kandoe.frontend.controller.resources.sessions.*;
import be.kdg.kandoe.frontend.controller.resources.sessions.create.CreateAsynchronousSessionResource;
import be.kdg.kandoe.frontend.controller.resources.sessions.create.CreateSessionResource;
import be.kdg.kandoe.frontend.controller.resources.sessions.create.CreateSynchronousSessionResource;
import be.kdg.kandoe.frontend.controller.resources.users.UserResource;
import be.kdg.kandoe.frontend.controller.rest.exceptions.CanDoControllerRuntimeException;
import lombok.val;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@PreAuthorize("isAuthenticated()")
public class SessionRestController {
    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private SessionGameService sessionGameService;

    @Autowired
    private CardService cardService;

    @Autowired
    private UserService userService;

    @Autowired
    private MapperFacade mapper;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{sessionId}", method = RequestMethod.GET)
    public ResponseEntity<SessionResource> getSession(@AuthenticationPrincipal User user,
                                                      @PathVariable("sessionId") int sessionId) {
        Session session = sessionService.getSessionById(sessionId);

        if (!session.isUserParticipant(user.getUserId()) && session.getOrganizer().getUserId() != user.getUserId())
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        SessionResource sessionResource = mapper.map(session, SessionResource.class);

        return new ResponseEntity<>(sessionResource, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createSession(@AuthenticationPrincipal User user,
                                        @Valid @RequestBody CreateSessionResource createSessionResource) {
        //TODO check if user is part of any organisation
        Category category = categoryService.getCategoryById(createSessionResource.getCategoryId());
        Topic topic = null;

        if (!category.getOrganization().isOrganizer(user))
            return new ResponseEntity(HttpStatus.FORBIDDEN);

        if (createSessionResource.getTopicId() != null)
            topic = topicService.getTopicByTopicId(createSessionResource.getTopicId());

        Session session = null;
        Class<? extends SessionResource> returnedResourceClass = null;

        if (createSessionResource instanceof CreateSynchronousSessionResource) {
            session = mapper.map(createSessionResource, SynchronousSession.class);
            returnedResourceClass = SynchronousSessionResource.class;
        } else if (createSessionResource instanceof CreateAsynchronousSessionResource) {
            session = mapper.map(createSessionResource, AsynchronousSession.class);
            returnedResourceClass = AsynchronousSessionResource.class;
        }

        if (session == null)
            throw new CanDoControllerRuntimeException("Session is of an unknown type", HttpStatus.BAD_REQUEST);

        session.setCategory(category);
        session.setTopic(topic);
        session.setOrganizer(user);

        session = sessionService.addSession(session);
        SessionResource returnedResource = mapper.map(session, returnedResourceClass);

        return new ResponseEntity(returnedResource, HttpStatus.CREATED);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{sessionId}/invite", method = RequestMethod.POST)
    public ResponseEntity inviteUser(@AuthenticationPrincipal User user,
                                     @PathVariable("sessionId") int sessionId,
                                     @RequestParam("userId") int userId) {
        Session session = sessionService.getSessionById(sessionId);
        User userToInvite = userService.getUserByUserId(userId);

        if (!session.getCategory().getOrganization().isOrganizer(user))
            return new ResponseEntity(HttpStatus.FORBIDDEN);

        sessionGameService.inviteUserForSession(session, userToInvite);

        return new ResponseEntity(HttpStatus.CREATED);
    }


    @RequestMapping(value = "/{sessionId}/join", method = RequestMethod.POST)
    public ResponseEntity joinSession(@AuthenticationPrincipal User user,
                                      @PathVariable("sessionId") int sessionId) {
        Session session = sessionService.getSessionById(sessionId);

        if (!session.isUserParticipant(user.getUserId()))
            return new ResponseEntity(HttpStatus.FORBIDDEN);

        sessionGameService.setUserJoined(session, user);

        return new ResponseEntity(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{sessionId}/cards", method = RequestMethod.POST)
    public ResponseEntity addCardToSession(@AuthenticationPrincipal User user,
                                           @PathVariable("sessionId") int sessionId,
                                           @Valid @RequestBody CreateCardDetailsResource createCardDetailsResource) {

        Session session = sessionService.getSessionById(sessionId);

        CardDetails cardDetails = mapper.map(createCardDetailsResource, CardDetails.class);
        sessionGameService.addCardDetails(session, user, cardDetails);

        return new ResponseEntity(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{sessionId}/cards", method = RequestMethod.PUT)
    public ResponseEntity setCardAddingDone(@AuthenticationPrincipal User user,
                                            @PathVariable("sessionId") int sessionId) {

        Session session = sessionService.getSessionById(sessionId);
        if (session.getOrganizer().getUserId() != user.getUserId()){
            return new ResponseEntity(HttpStatus.CONFLICT);
        }
        sessionGameService.confirmAddedCards(session);

        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/{sessionId}/reviews", method = RequestMethod.PUT)
    public ResponseEntity setCardReviewsDone(@AuthenticationPrincipal User user,
                                            @PathVariable("sessionId") int sessionId) {

        Session session = sessionService.getSessionById(sessionId);
        if (session.getOrganizer().getUserId() != user.getUserId()){
            return new ResponseEntity(HttpStatus.CONFLICT);
        }
        sessionGameService.confirmReviews(session);

        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/{sessionId}/cards/{cardId}", method = RequestMethod.POST)
    public ResponseEntity chooseCardByUser(@AuthenticationPrincipal User user,
                                           @PathVariable("sessionId") int sessionId,
                                           @PathVariable("cardId") int cardId) {
        Session session = sessionService.getSessionById(sessionId);
        CardDetails cardDetails = cardService.getCardDetailsById(cardId);
        sessionGameService.chooseCards(session, user, cardDetails);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    /*public ResponseEntity startSession(@AuthenticationPrincipal User user, @RequestParam("sessionId") int sessionId) {
        
        
        return new ResponseEntity(HttpStatus.CREATED);
    }*/

    //@PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/add/{sessionId}", method = RequestMethod.POST)
    public ResponseEntity addUserToSession(@AuthenticationPrincipal User user,
                                           @RequestParam int userId, @PathVariable int sessionId) {
        /*Session session = sessionService.getSessionById(sessionId);
        User userToAdd = userService.getUserByUserId(userId);

        if (session == null)
            throw new CanDoControllerRuntimeException(String.format("No session with id %d", sessionId));

        if (userToAdd == null)
            throw new CanDoControllerRuntimeException(String.format("No user with id %d", userId));

        Organization organization = session.getOrganization();
        User sessionOwner = organization.getOwner();

        boolean isOwner = sessionOwner.equals(user);
        boolean isParticipant = session.isUserParticipant(user.getUserId());

        //Stop if the user that is adding someone isn't owner or isn't a participant
        if( ! (isOwner || isParticipant)){
            throw new CanDoControllerRuntimeException("User isn't part of session and as such cannot add another user", HttpStatus.BAD_REQUEST);
        }

        if (session.addParticipant(userToAdd)){
            sessionService.updateSession(session);
            return new ResponseEntity(HttpStatus.OK);
        }*/

        throw new CanDoControllerRuntimeException("User %d is already participating in this session", HttpStatus.BAD_REQUEST);
    }

    /*private ResponseEntity createSessionHelperMethod(User user, CreateSessionResource resource) {
        List<Organization> organizations = organizationService.getOrganizationsByOwner(user.getUsername());
        val organizationOptional = organizations.stream().filter(o -> o.getOrganizationId() == resource.getOrganizationId()).findAny();
        if (organizationOptional.isPresent()) {
            Organization organization = organizationOptional.get();
            if (organization.getOwner().getUserId() != user.getUserId()) {
                throw new CanDoControllerRuntimeException(String.format("User (%s) is not the owner of the organization (%d)", user.getUsername(), resource.getOrganizationId()), HttpStatus.BAD_REQUEST);
            } else {
                //TODO mooier manier voor vinden?
                Class modelClass = null;
                Class returnResourceClass = null;
                if (resource instanceof CreateSynchronousSessionResource) {
                    modelClass = SynchronousSession.class;
                    returnResourceClass = SynchronousSessionResource.class;
                } else if (resource instanceof CreateAsynchronousSessionResource) {
                    modelClass = AsynchronousSession.class;
                    returnResourceClass = AsynchronousSessionResource.class;
                } else {
                    throw new CanDoControllerRuntimeException(String.format("Resource is not a known type of Session (%s).", resource.getClass().getSimpleName()), HttpStatus.UNPROCESSABLE_ENTITY);
                }

                Session session = (Session) mapper.map(resource, modelClass);
                if (resource.getTopicId() != null) {
                    Topic topic = topicService.getTopicByTopicId(resource.getTopicId());
                    session.setTopic(topic);
                }
                session.setOrganization(organization);
                session.setOrganizer(user);
                Session savedSession = sessionService.addSession(session);
                SessionResource returnSessionResource = (SessionResource) mapper.map(savedSession, returnResourceClass);
                return new ResponseEntity<SessionResource>(returnSessionResource, HttpStatus.CREATED);
            }
        }
        throw new CanDoControllerRuntimeException(String.format("User (%s) is not the owner of an organization", user.getUsername()), HttpStatus.BAD_REQUEST);
    }*/
}
