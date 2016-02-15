package be.kdg.kandoe.frontend.controller.rest;

import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.UserService;
import be.kdg.kandoe.frontend.controller.resources.users.CreateUserResource;
import be.kdg.kandoe.frontend.controller.resources.users.UserResource;
import ma.glasnost.orika.MapperFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystem;

@RestController
@RequestMapping("/api/users")
public class UserRestController {
    private Logger logger = LoggerFactory.getLogger(UserRestController.class);

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

    //TODO: file upload is image and not null
    @RequestMapping(value = "/{userId}/photo", method = RequestMethod.POST)
    public HttpStatus uploadPhoto(@PathVariable int userId, @AuthenticationPrincipal User user, @RequestParam("file") MultipartFile uploadedFile, HttpServletRequest servletRequest) throws IOException {
        if (userId != user.getUserId()){
            return HttpStatus.BAD_REQUEST;
        }
        if (!uploadedFile.isEmpty()) {
            try {
                String fileSeperator = File.separator;
                String path = servletRequest.getSession().getServletContext().getRealPath("/") + fileSeperator + "profilepictures" + fileSeperator + userId + fileSeperator + uploadedFile.getName();
                System.out.println(String.format("Path to save file: %s", path));
                File saveFile = new File(path);
                if (!saveFile.getParentFile().exists()){
                    saveFile.getParentFile().mkdirs();
                }
                if (!saveFile.exists()){
                    saveFile.createNewFile();
                }
                byte[] bytes = uploadedFile.getBytes();
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(saveFile));
                stream.write(bytes);
                stream.close();
            } catch (Exception e) {
                throw e;
            }
        } else {
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.ACCEPTED;
    }
}
