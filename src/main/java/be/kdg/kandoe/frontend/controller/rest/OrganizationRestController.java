package be.kdg.kandoe.frontend.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.OrganizationService;
import be.kdg.kandoe.frontend.controller.resources.organizations.OrganizationResource;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationRestController {
    @Autowired
    private OrganizationService organizationService;
    
    /*public ResponseEntity<OrganizationResource> createOrganization(@AuthenticationPrincipal User user,
                                                                   @Valid @RequestBody OrganizationResource organizationResource) {
        
    }*/
}
