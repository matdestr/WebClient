package be.kdg.kandoe.frontend.controller.resources.organizations;

import be.kdg.kandoe.frontend.controller.resources.users.UserResource;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

@Data
public class OrganizationResource {
    private int organizationId;

    private String name;
    
    private UserResource owner;
    private List<UserResource> members;
}
