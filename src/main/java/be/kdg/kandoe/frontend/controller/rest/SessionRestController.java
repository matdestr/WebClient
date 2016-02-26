package be.kdg.kandoe.frontend.controller.rest;

import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.organizations.Topic;
import be.kdg.kandoe.backend.model.sessions.AsynchronousSession;
import be.kdg.kandoe.backend.model.sessions.Session;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.OrganizationService;
import be.kdg.kandoe.backend.service.api.SessionService;
import be.kdg.kandoe.backend.service.api.TopicService;
import be.kdg.kandoe.frontend.controller.resources.sessions.AsynchronousSessionResource;
import be.kdg.kandoe.frontend.controller.resources.sessions.CreateAsynchronousSessionResource;
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
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createSession(@AuthenticationPrincipal User user, @Valid @RequestBody CreateAsynchronousSessionResource createAsynchronousSessionResource){
        List<Organization> organizations = organizationService.getOrganizationsByOwner(user.getUsername());
        val organizationOptional = organizations.stream().filter(o -> o.getOrganizationId() == createAsynchronousSessionResource.getOrganizationId()).findAny();
        if (organizationOptional.isPresent()){
            Organization organization = organizationOptional.get();
            if (organization.getOwner().getUserId() != user.getUserId()){
                throw new CanDoControllerRuntimeException(String.format("User (%s) is not the owner of the organization (%d)", user.getUsername(), createAsynchronousSessionResource.getOrganizationId()), HttpStatus.BAD_REQUEST);
            } else {
                AsynchronousSession session = mapper.map(createAsynchronousSessionResource, AsynchronousSession.class);
                if (createAsynchronousSessionResource.getTopicId() != null){
                    Topic topic = topicService.getTopicByTopicId(createAsynchronousSessionResource.getTopicId());
                    session.setTopic(topic);
                }
                session.setOrganizer(user);
                AsynchronousSession savedSession = (AsynchronousSession) sessionService.addSession(session);
                AsynchronousSessionResource asynchronousSessionResource = mapper.map(savedSession, AsynchronousSessionResource.class);
                return new ResponseEntity<AsynchronousSessionResource>(asynchronousSessionResource, HttpStatus.CREATED);
            }
        } else {
            throw new CanDoControllerRuntimeException(String.format("User (%s) is not the owner of an organization", user.getUsername()), HttpStatus.BAD_REQUEST);
        }
    }

}