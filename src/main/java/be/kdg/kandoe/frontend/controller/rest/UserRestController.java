package be.kdg.kandoe.frontend.controller.rest;

import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.UserService;
import be.kdg.kandoe.frontend.controller.resources.users.CreateUserResource;
import be.kdg.kandoe.frontend.controller.resources.users.UserResource;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserRestController {
    @Autowired
    private MapperFacade mapper;

    @Autowired
    private UserService userService;
    
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<UserResource> createUser(@Valid @RequestBody CreateUserResource createUserResource){
        User userIn = mapper.map(createUserResource, User.class);
        User userOut = userService.addUser(userIn);
        return new ResponseEntity<UserResource>(mapper.map(userOut, UserResource.class), HttpStatus.CREATED);
    }
}
