package be.kdg.kandoe.frontend.controller.rest;

import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.organizations.Topic;
import be.kdg.kandoe.backend.model.sessions.AsynchronousSession;
import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.backend.model.sessions.SynchronousSession;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.OrganizationService;
import be.kdg.kandoe.backend.service.api.SessionService;
import be.kdg.kandoe.backend.service.api.TopicService;
import be.kdg.kandoe.backend.service.api.UserService;
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
import javax.validation.constraints.Null;
import java.util.List;

@RestController
@RequestMapping("api/sessions")
public class SessionRestController {
    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserService userService;

    @Autowired
    private MapperFacade mapper;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createSession(@AuthenticationPrincipal User user, @Valid @RequestBody CreateSynchronousSessionResource createSynchronousSessionResource) {
        return createSessionHelperMethod(user, createSynchronousSessionResource);
    }


    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/add/{sessionId}", method = RequestMethod.POST)
    public ResponseEntity addUserToSession(@AuthenticationPrincipal User user, @RequestParam int userId, @PathVariable int sessionId){
        Session session = sessionService.getSessionById(sessionId);
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
        }

        throw new CanDoControllerRuntimeException("User %d is already participating in this session", HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity createSessionHelperMethod(User user, CreateSessionResource resource) {
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
    }

}