package be.kdg.kandoe.frontend.controller.rest;

import be.kdg.kandoe.backend.model.users.User;
import be.kdg.kandoe.backend.service.api.UserService;
import be.kdg.kandoe.frontend.controller.resources.users.CreateUserResource;
import be.kdg.kandoe.frontend.controller.resources.users.UpdateUserResource;
import be.kdg.kandoe.frontend.controller.resources.users.UserResource;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
        userService.isUsernameAvailable(createUserResource.getUsername());
        User userIn = mapper.map(createUserResource, User.class);
        User userOut = userService.addUser(userIn);
        return new ResponseEntity<UserResource>(mapper.map(userOut, UserResource.class), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{username}", method = RequestMethod.GET)
    public ResponseEntity<UserResource> getUserByName(@PathVariable String username) {

        User user = userService.getUserByUsername(username);

        if (user == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(mapper.map(user, UserResource.class), HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{userId}/photo", method = RequestMethod.POST)
    public ResponseEntity uploadPhoto(@PathVariable int userId, @AuthenticationPrincipal User user, @RequestParam("file") MultipartFile uploadedFile, HttpServletRequest servletRequest) throws IOException {
        if (userId != user.getUserId()){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        if (uploadedFile == null){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
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

                BufferedImage image = ImageIO.read(uploadedFile.getInputStream());

                if (image == null){
                    return new ResponseEntity(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
                }

                String extension = FilenameUtils.getExtension(saveFile.getPath());
                ImageIO.write(image, extension, saveFile);

            } catch (Exception e) {
                throw e;
            }
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{userId}", method = RequestMethod.PUT)
    public ResponseEntity<UserResource> updateProfile(@PathVariable int userId, @AuthenticationPrincipal User user,@Valid @RequestBody UpdateUserResource resource) {
        if (userId != user.getUserId()){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        userService.checkLogin(userId, resource.getVerifyPassword());
        if (!user.getUsername().equals(resource.getUsername())){
            userService.isUsernameAvailable(resource.getUsername());
            user.setUsername(resource.getUsername());
        }
        user.setEmail(resource.getEmail());
        user.setName(resource.getName());
        user.setSurname(resource.getSurname());
        User updatedUser = userService.updateUser(user);
        return new ResponseEntity<UserResource>(mapper.map(updatedUser, UserResource.class), HttpStatus.OK);
    }
}
