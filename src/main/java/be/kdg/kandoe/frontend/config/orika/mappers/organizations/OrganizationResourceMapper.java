package be.kdg.kandoe.frontend.config.orika.mappers.organizations;

import be.kdg.kandoe.backend.model.organizations.Organization;
import be.kdg.kandoe.frontend.controller.resources.organizations.OrganizationResource;
import be.kdg.kandoe.frontend.controller.resources.users.UserResource;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.springframework.stereotype.Component;

@Component
public class OrganizationResourceMapper extends CustomMapper<Organization, OrganizationResource>{
    @Override
    public void mapAtoB(Organization organization, OrganizationResource organizationResource, MappingContext context) {
        organizationResource.setMembers(mapperFacade.mapAsList(organization.getOrganizers(), UserResource.class));
    }

    @Override
    public void mapBtoA(OrganizationResource organizationResource, Organization organization, MappingContext context) {

    }
}
