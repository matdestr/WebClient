package be.kdg.kandoe.frontend.config.orika.mappers;

import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.frontend.controller.resources.users.CreateUserResource;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

public class CreateUserResourceMapper extends CustomMapper<User, CreateUserResource> {
    @Override
    public void mapBtoA(CreateUserResource createUserResource, User user, MappingContext context) {

    }

    @Override
    public void mapAtoB(User user, CreateUserResource createUserResource, MappingContext context) {

    }
}
