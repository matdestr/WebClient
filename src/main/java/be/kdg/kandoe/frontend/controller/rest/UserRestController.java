package be.kdg.kandoe.frontend.controller.rest;

import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.UserService;
import be.kdg.kandoe.frontend.controller.resources.users.CreateUserResource;
import be.kdg.kandoe.frontend.controller.resources.users.UserResource;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Controller
@RequestMapping("/api/users")
public class UserRestController {
    //TODO: password encoder
    @Autowired
    private MapperFacade mapper;

    @Autowired
    private UserService userService;



    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<UserResource> createUser(@Valid @RequestBody CreateUserResource createUserResource){
        User user_in = new User(createUserResource.getUsername(), createUserResource.getPassword());
        user_in.setEmail(createUserResource.getEmail());
        User user_out = userService.addUser(user_in);
        return new ResponseEntity<UserResource>(mapper.map(user_out, UserResource.class), HttpStatus.CREATED);
    }


}
