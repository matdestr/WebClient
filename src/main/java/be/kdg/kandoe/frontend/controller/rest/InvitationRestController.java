
package be.kdg.kandoe.frontend.controller.rest;

import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.backend.model.users.Invitation;
import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.InvitationService;
import be.kdg.kandoe.backend.service.api.OrganizationService;
import be.kdg.kandoe.frontend.controller.resources.invitations.InvitationResource;
import be.kdg.kandoe.frontend.controller.rest.exceptions.CanDoControllerRuntimeException;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/invitations")
public class InvitationRestController {
    @Autowired
    private InvitationService invitationService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private MapperFacade mapperFacade;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity acceptInvite(@AuthenticationPrincipal User user, @RequestParam("acceptId") String acceptId, @RequestParam("organizationId") int organizationId){
        Invitation invitation = invitationService.getInvitationByAcceptId(acceptId);

        if (invitation == null) {
            throw new CanDoControllerRuntimeException("Invitation does not exist.");
        }

        int invitationOrganizationId = invitation.getOrganization().getOrganizationId();
        int userId = invitation.getInvitedUser().getUserId();

        if (user.getUserId() != userId){
            throw new CanDoControllerRuntimeException("Logged in user and the invited user their id's don't match.", HttpStatus.UNAUTHORIZED);
        }

        if (organizationId != invitationOrganizationId){
            throw new CanDoControllerRuntimeException("Invite does not belong to this organization.", HttpStatus.BAD_REQUEST);
        }

        Organization organization = organizationService.getOrganizationById(invitationOrganizationId);
        organization.addMember(user);
        organizationService.updateOrganization(organization);

        invitationService.invalidateInvitation(invitation);

        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/decline", method = RequestMethod.GET)
    public ResponseEntity declineInvite(@AuthenticationPrincipal User user, @RequestParam("acceptId") String acceptId, @RequestParam("organizationId") int organizationId){
        Invitation invitation = invitationService.getInvitationByAcceptId(acceptId);

        if (invitation == null){
            throw new CanDoControllerRuntimeException("Invitation does not exist.");
        }

        int invitationOrganizationId = invitation.getOrganization().getOrganizationId();
        int userId = invitation.getInvitedUser().getUserId();

        if (user.getUserId() != userId){
            throw new CanDoControllerRuntimeException("Logged in user and the invited user their id's don't match.", HttpStatus.UNAUTHORIZED);
        }

        if (organizationId != invitationOrganizationId){
            throw new CanDoControllerRuntimeException("Invite does not belong to this organization.", HttpStatus.BAD_REQUEST);
        }

        invitationService.invalidateInvitation(invitation);

        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/open", method = RequestMethod.GET)
    public List<InvitationResource> getOpenInvitationsForUser(@AuthenticationPrincipal User user, @RequestParam("userId") int userId){
        if (user.getUserId() != userId){
            throw new CanDoControllerRuntimeException("Logged in user and the invited user their id's don't match.", HttpStatus.UNAUTHORIZED);
        }

        List<Invitation> invitations = invitationService.getInvitationsByUserId(userId);
        return mapperFacade.mapAsList(invitations, InvitationResource.class);
    }
}

