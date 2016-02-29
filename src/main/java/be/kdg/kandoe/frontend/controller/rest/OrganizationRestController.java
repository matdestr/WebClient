package be.kdg.kandoe.frontend.controller.rest;

import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.OrganizationService;
import be.kdg.kandoe.backend.service.api.UserService;
import be.kdg.kandoe.frontend.controller.resources.organizations.OrganizationResource;
import be.kdg.kandoe.frontend.controller.rest.exceptions.CanDoControllerRuntimeException;
import ma.glasnost.orika.MapperFacade;
import org.bouncycastle.cert.ocsp.Req;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationRestController {
    @Autowired
    private OrganizationService organizationService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private MapperFacade mapperFacade;
    
    @RequestMapping(method = RequestMethod.POST)
    //@PreAuthorize("hasAnyRole('ROLE_CLIENT')")
    public ResponseEntity<OrganizationResource> createOrganization(@AuthenticationPrincipal User user,
                                                                   @Valid @RequestBody OrganizationResource organizationResource) {
        Organization organization = new Organization(organizationResource.getName(), user);
        organization = organizationService.addOrganization(organization);
        
        //mapperFacade.map(organization, organizationResource);
        OrganizationResource resultResource = mapperFacade.map(organization, OrganizationResource.class);
        
        return new ResponseEntity<>(resultResource, HttpStatus.CREATED);
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

}
