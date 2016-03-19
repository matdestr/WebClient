package be.kdg.kandoe.frontend.controller.rest;

import be.kdg.kandoe.backend.model.organizations.Category;
import be.kdg.kandoe.backend.model.organizations.Topic;
import be.kdg.kandoe.backend.model.sessions.AsynchronousSession;
import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.backend.model.sessions.SynchronousSession;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.CategoryService;
import be.kdg.kandoe.backend.service.api.OrganizationService;
import be.kdg.kandoe.backend.service.api.SessionService;
import be.kdg.kandoe.backend.service.api.TopicService;
import be.kdg.kandoe.frontend.controller.resources.sessions.AsynchronousSessionResource;
import be.kdg.kandoe.frontend.controller.resources.sessions.SessionListItemResource;
import be.kdg.kandoe.frontend.controller.resources.sessions.SessionResource;
import be.kdg.kandoe.frontend.controller.resources.sessions.SynchronousSessionResource;
import be.kdg.kandoe.frontend.controller.resources.sessions.create.CreateAsynchronousSessionResource;
import be.kdg.kandoe.frontend.controller.resources.sessions.create.CreateSessionResource;
import be.kdg.kandoe.frontend.controller.resources.sessions.create.CreateSynchronousSessionResource;
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
    private MapperFacade mapper;

    @RequestMapping(value = "/{sessionId}", method = RequestMethod.GET)
    public ResponseEntity<SessionResource> getSession(@AuthenticationPrincipal User user,
                                                      @PathVariable("sessionId") int sessionId) {
        Session session = sessionService.getSessionById(sessionId);

        if (!session.isUserParticipant(user.getUserId()) && session.getOrganizer().getUserId() != user.getUserId())
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        SessionResource sessionResource = null;

        if (session instanceof SynchronousSession) {
            sessionResource = mapper.map(session, SynchronousSessionResource.class);
        } else {
           sessionResource = mapper.map(session,AsynchronousSessionResource.class);
        }


        return new ResponseEntity<>(sessionResource, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createSession(@AuthenticationPrincipal User user,
                                        @Valid @RequestBody CreateSessionResource createSessionResource) {
        if (organizationService.getOrganizationsOfMember(user.getUsername()).isEmpty()){
            return new ResponseEntity("User is not owner or member of any organization", HttpStatus.BAD_REQUEST);
        }
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


    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<SessionListItemResource>> getSessionsUser(@AuthenticationPrincipal User user){
        List<Session> sessions = sessionService.getSessionsUser(user.getUserId());
        return new ResponseEntity<>(mapper.mapAsList(sessions, SessionListItemResource.class), HttpStatus.OK);
    }
}
