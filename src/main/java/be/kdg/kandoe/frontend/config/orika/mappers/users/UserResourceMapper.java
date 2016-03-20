package be.kdg.kandoe.frontend.config.orika.mappers.users;

import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.frontend.controller.resources.users.UserResource;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.springframework.stereotype.Component;

/**
 * Orika mapper for User and UserResource
 */

@Component
public class UserResourceMapper extends CustomMapper<User, UserResource> {
    @Override
    public void mapAtoB(User user, UserResource userResource, MappingContext context) {
    }

    @Override
    public void mapBtoA(UserResource userResource, User user, MappingContext context) {

    }
}
