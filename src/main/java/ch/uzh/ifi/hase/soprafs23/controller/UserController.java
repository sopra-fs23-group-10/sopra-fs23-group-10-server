package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserGetDTO> getUsers(@RequestHeader("token") String token) {
        // verify token
        userService.verifyToken(token);

        // fetch all users in the internal representation
        List<User> users = userService.getUsers();
        List<UserGetDTO> userGetDTOs = new ArrayList<>();
        users.sort(Comparator.comparingLong(User::getRank));

        // convert each user to the API representation
        for (User user : users) {
            userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTONoToken(user));
        }
        return userGetDTOs;
    }

    @GetMapping("/users/online")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserGetDTO> getOnlineUsers(@RequestHeader("token") String token) {
        // verify token
        userService.verifyToken(token);

        // fetch all users in the internal representation
        List<User> users = userService.getOnlineUsers();
        List<UserGetDTO> userGetDTOs = new ArrayList<>();


        // convert each user to the API representation
        for (User user : users) {
            userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTONoToken(user));
        }
        return userGetDTOs;
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO) {
        // convert API user to internal representation
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        // check that username, email and password are not empty or null
        String baseErrorMessage = "%s cannot be empty or null, but this seems to be the case.";
        if ((userInput.getUsername() == null) || userInput.getUsername().equals("")
                || (userInput.getPassword() == null) || userInput.getPassword().equals("")
                || (userInput.getEmail() == null) || userInput.getEmail() .equals("")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format(baseErrorMessage, "Username, Email and/or Password"));
        }

        // create user
        User createdUser = userService.createUser(userInput);

        // convert internal representation of user back to API
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
    }

    @GetMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO getUserById(@PathVariable (name = "userId") long userId, @RequestHeader("token") String token) {
        // verify token
        userService.verifyToken(token);

        // search for user by id (path variable)
        User user = userService.searchUserById(userId);

        // convert the returned user to API user (without token)
        // return the API user
        return DTOMapper.INSTANCE.convertEntityToUserGetDTONoToken(user);
    }

    @PutMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public UserGetDTO putUser(@PathVariable (name = "userId") long userId, @RequestHeader("token") String token, @RequestBody UserPutDTO userPutDTO) {
        // verify token with userId from path variable to authorise changes
        User userToChange = userService.verifyTokenWithId(token, userId);

        // convert the user from request body to entry
        User userInput = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);

        if (userInput.getUsername() == null || userInput.getUsername().equals("")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username cannot be empty or null.");
        }

        // make changes to user
        userService.changeUsernameAndProfilePic(userId, userInput);

        return DTOMapper.INSTANCE.convertEntityToUserGetDTONoToken(userToChange);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO login(@RequestBody UserPostDTO userPostDTO) {
        // convert API user to internal representation
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        if ((userInput.getUsername() == null) || userInput.getUsername().equals("") ||
                (userInput.getPassword() == null) || userInput.getPassword().equals("")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Username and Password cannot be empty or null.");
        }

        // validate username and password
        User checkedUser = userService.checkLoginCredentials(userInput.getUsername(), userInput.getPassword());

        // set user status to ONLINE
        userService.setOnline(checkedUser.getId());

        // return validated user (with token)
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(checkedUser);
    }

    @PostMapping("/logout/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO logout(@PathVariable (name = "userId") long userId, @RequestHeader("token") String token) {
        // verify token and get matching user
        User user = userService.verifyTokenWithId(token, userId);

        // set user status to OFFLINE
        userService.setOffline(userId);

        // convert user to API user and return (without token)
        return DTOMapper.INSTANCE.convertEntityToUserGetDTONoToken(user);
    }

    @PostMapping("/reset")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void sendEmail(@RequestBody UserPostDTO userPostDTO) {
        User user = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
        userService.sendNewPassword(user);
    }
}
