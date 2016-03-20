package be.kdg.kandoe.frontend.controller.resources.invitations;

import be.kdg.kandoe.frontend.controller.resources.organizations.OrganizationResource;
import be.kdg.kandoe.frontend.controller.resources.users.UserResource;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Resource for the Invitation model
 */

@Data
@NoArgsConstructor
public class InvitationResource {
    private UserResource invitedUser;
    private OrganizationResource organization;
    private String acceptId;

    private boolean accepted;
}
