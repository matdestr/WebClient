
package be.kdg.kandoe.frontend.controller.rest;

import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.users.Invitation;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.InvitationService;
import be.kdg.kandoe.backend.service.api.OrganizationService;
import be.kdg.kandoe.frontend.controller.rest.exceptions.CanDoControllerRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/invitations")
public class InvitationRestController {
    @Autowired
    private InvitationService invitationService;

    @Autowired
    private OrganizationService organizationService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity acceptInvite(@AuthenticationPrincipal User user, @RequestParam("acceptId") String acceptId){
        Invitation invitation = invitationService.getInvitationByAcceptId(acceptId);

        if (invitation == null) {
            throw new CanDoControllerRuntimeException("This invitation has already been accepted.");
        }

        int organizationId = invitation.getOrganization().getOrganizationId();
        int userId = invitation.getInvitedUser().getUserId();

        if (user.getUserId() != userId){
            throw new CanDoControllerRuntimeException("Logged in user and the invited user their id's don't match.", HttpStatus.UNAUTHORIZED);
        }

        Organization organization = organizationService.getOrganizationById(organizationId);
        organization.addOrganizer(user);
        organizationService.updateOrganization(organization);

        invitationService.invitationAccepted(invitation);

        return new ResponseEntity(HttpStatus.OK);
    }
}

