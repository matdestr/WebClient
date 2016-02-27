package be.kdg.kandoe.frontend.config.orika.mappers.users;

import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.frontend.controller.resources.users.UpdateUserResource;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

/**
 * Created by Wannes on 16/02/16.
 */
public class UpdateUserResourceMapper extends CustomMapper<User, UpdateUserResource> {
    @Override
    public void mapAtoB(User user, UpdateUserResource userResource, MappingContext context) {

    }

    @Override
    public void mapBtoA(UpdateUserResource userResource, User user, MappingContext context) {
    }
}
