package be.kdg.kandoe.frontend.controller.rest;

import be.kdg.kandoe.backend.model.organizations.Organization;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.OrganizationService;
import be.kdg.kandoe.frontend.controller.resources.organizations.OrganizationResource;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationRestController {
    @Autowired
    private OrganizationService organizationService;
    
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
        
        return new ResponseEntity<OrganizationResource>(resultResource, HttpStatus.CREATED);
    }
    
    @RequestMapping(value = "/{organizationId}", method = RequestMethod.GET)
    public ResponseEntity<OrganizationResource> findOrganization(@PathVariable("organizationId") int organizationId) {
        Organization organization = organizationService.getOrganizationById(organizationId);
        OrganizationResource resource = mapperFacade.map(organization, OrganizationResource.class);
        
        return new ResponseEntity<OrganizationResource>(resource, HttpStatus.OK);
    }
}
