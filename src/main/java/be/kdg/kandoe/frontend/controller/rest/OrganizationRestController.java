package be.kdg.kandoe.frontend.controller.rest;

import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.EmailService;
import be.kdg.kandoe.backend.service.api.OrganizationService;
import be.kdg.kandoe.backend.service.api.UserService;
import be.kdg.kandoe.frontend.controller.resources.organizations.CreateOrganizationResource;
import be.kdg.kandoe.frontend.controller.resources.organizations.EmailResource;
import be.kdg.kandoe.frontend.controller.resources.organizations.OrganizationResource;
import be.kdg.kandoe.frontend.controller.rest.exceptions.CanDoControllerRuntimeException;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationRestController {
    @Autowired
    private OrganizationService organizationService;
    
    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private MapperFacade mapperFacade;
    
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createOrganization(@AuthenticationPrincipal User user,
                                             @Valid @RequestBody CreateOrganizationResource organizationResource) {

        boolean organizationExists = organizationService.organizationExists(organizationResource.getName());

        if (organizationExists)
            throw new CanDoControllerRuntimeException(String.format("An organization with the name %s already exists", organizationResource.getName()), HttpStatus.CONFLICT);

        Organization organization = new Organization(organizationResource.getName(), user);
        organization = organizationService.addOrganization(organization);

        if (! organizationResource.getEmails().isEmpty())
            inviteUsers(organization, user, organizationResource.getEmails());


        OrganizationResource resultResource = mapperFacade.map(organization, OrganizationResource.class);
        
        return new ResponseEntity<>(resultResource, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity updateOrganization(@AuthenticationPrincipal User user,
                                             @Valid @RequestBody OrganizationResource resource){
        Organization organization = mapperFacade.map(resource, Organization.class);

        if (! user.equals(organization.getOwner())){
            throw new CanDoControllerRuntimeException("User is not owner of organization and isn't allowed to update it.", HttpStatus.BAD_REQUEST);
        }

        organizationService.updateOrganization(organization);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/add/{organizationId}", method = RequestMethod.POST)
    public ResponseEntity addUsersToOrganization(@AuthenticationPrincipal User user,
                                                 @PathVariable("organizationId") int organizationId,
                                                 @Valid @RequestBody List<EmailResource> emails){
        Organization organization = organizationService.getOrganizationById(organizationId);

        if (organization == null)
            throw new CanDoControllerRuntimeException("no organization with id " + organizationId, HttpStatus.NOT_FOUND);

        if (! emails.isEmpty()) {

            AtomicBoolean canAdd = new AtomicBoolean(false);
            int ownerId = organization.getOwner().getUserId();
            if (ownerId == user.getUserId())
                canAdd.set(true);

            //don't loop if the owner was the requester
            if (!canAdd.get())
                for (User organizer : organization.getOrganizers()) {
                    if (organizer.getUserId() == user.getUserId())
                        canAdd.set(true);
                    break;
                }

            if (canAdd.get()) {
                this.inviteUsers(organization, user, emails);
            } else {
                throw new CanDoControllerRuntimeException("user isn't allowed to add users to organization", HttpStatus.UNAUTHORIZED);
            }
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<OrganizationResource> getOrganizations(){
        return mapperFacade.mapAsList(organizationService.getOrganizations(), OrganizationResource.class);
    }
    
    @RequestMapping(value = "/{organizationId}", method = RequestMethod.GET)
    public ResponseEntity<OrganizationResource> findOrganization(@PathVariable("organizationId") int organizationId) {
        Organization organization = organizationService.getOrganizationById(organizationId);
        OrganizationResource resource = mapperFacade.map(organization, OrganizationResource.class);

        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @RequestMapping(value = "/user/{user}", method = RequestMethod.GET)
    public ResponseEntity<List<OrganizationResource>> findOrganizationsByUser(@PathVariable("user") String username, @RequestParam(value = "owner", defaultValue = "false", required = false) boolean isOwner) {
        User user = userService.getUserByUsername(username);
        if (user == null){
            throw new CanDoControllerRuntimeException(
                    String.format("User with username %s does not exist", username),
                    HttpStatus.NOT_FOUND);
        }

        List<Organization> organizations = isOwner ? organizationService.getOrganizationsByOwner(username): organizationService.getOrganizationsOfMember(username);

        List<OrganizationResource> resources =  organizations.stream()
                .map(o -> mapperFacade.map(o, OrganizationResource.class))
                .collect(Collectors.toList());

        return new ResponseEntity<>(resources, HttpStatus.OK);
    }

    @RequestMapping(value = "/{organizationId}", method = RequestMethod.PUT)
    public ResponseEntity setOrganizationName(@PathVariable("organizationId") int organizationId, @RequestParam(value="organizationName")String organizationName){
        Organization organization = organizationService.getOrganizationById(organizationId);
        organization.setName(organizationName);
        organizationService.updateOrganization(organization);
        OrganizationResource resource = mapperFacade.map(organization, OrganizationResource.class);

        return new ResponseEntity<>(resource,HttpStatus.OK);
    }

    private void inviteUsers(Organization organization, User user, List<EmailResource> emailResources){
        List<String> emails = new ArrayList<>();
        List<User> users = new ArrayList<>();

        if (emailResources != null && ! emailResources.isEmpty()) {
            //Retrieve existing users
            for (EmailResource mailObject : emailResources) {
                String email = mailObject.getEmail();

                if (email != null) {
                    User requested = userService.getUserByEmail(email);
                    if (requested != null) {
                        users.add(requested);
                    } else {
                        emails.add(email);
                    }
                }
            }
        }

        emailService.inviteUsersToOrganization(organization, user, users);
        emailService.inviteUnexistingUsersToOrganization(organization, user, emails);
    }
}
