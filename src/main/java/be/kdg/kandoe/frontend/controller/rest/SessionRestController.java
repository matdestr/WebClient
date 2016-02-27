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
import be.kdg.kandoe.frontend.controller.resources.sessions.SessionResource;
import be.kdg.kandoe.frontend.controller.resources.sessions.*;
import be.kdg.kandoe.frontend.controller.resources.sessions.create.CreateAsynchronousSessionResource;
import be.kdg.kandoe.frontend.controller.resources.sessions.create.CreateSessionResource;
import be.kdg.kandoe.frontend.controller.resources.sessions.create.CreateSynchronousSessionResource;
import be.kdg.kandoe.frontend.controller.rest.exceptions.CanDoControllerRuntimeException;
import lombok.val;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
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
    private MapperFacade mapper;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/asynchronous", method = RequestMethod.POST)
    public ResponseEntity createSession(@AuthenticationPrincipal User user, @Valid @RequestBody CreateAsynchronousSessionResource createAsynchronousSessionResource) {
        return createSessionHelperMethod(user, createAsynchronousSessionResource);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/synchronous", method = RequestMethod.POST)
    public ResponseEntity createSession(@AuthenticationPrincipal User user, @Valid @RequestBody CreateSynchronousSessionResource createSynchronousSessionResource) {
        return createSessionHelperMethod(user, createSynchronousSessionResource);
    }

    private ResponseEntity createSessionHelperMethod(User user, CreateSessionResource resource) {
        List<Organization> organizations = organizationService.getOrganizationsByOwner(user.getUsername());
        val organizationOptional = organizations.stream().filter(o -> o.getOrganizationId() == resource.getOrganizationId()).findAny();
        if (organizationOptional.isPresent()) {
            Organization organization = organizationOptional.get();
            if (organization.getOwner().getUserId() != user.getUserId()) {
                throw new CanDoControllerRuntimeException(String.format("User (%s) is not the owner of the organization (%d)", user.getUsername(), resource.getOrganizationId()), HttpStatus.BAD_REQUEST);
            } else {
                Class modelClass = null;
                Class returnResourceClass = null;
                if (resource instanceof CreateSynchronousSessionResource) {
                    modelClass = SynchronousSession.class;
                    returnResourceClass = SynchronousSessionResource.class;
                } else if (resource instanceof CreateAsynchronousSessionResource) {
                    modelClass = AsynchronousSession.class;
                    returnResourceClass = AsynchronousSessionResource.class;
                } else {
                    throw new CanDoControllerRuntimeException(String.format("Resource is not a known type of Session."), HttpStatus.UNPROCESSABLE_ENTITY);
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