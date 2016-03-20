package be.kdg.kandoe.frontend.controller.resources.organizations;

import be.kdg.kandoe.frontend.controller.resources.users.UserResource;
import lombok.Data;

import java.util.List;

/**
 * Resource for an organization
 */

@Data
public class OrganizationResource {
    private int organizationId;

    private String name;
    
    private UserResource owner;
    private List<UserResource> members;
}
