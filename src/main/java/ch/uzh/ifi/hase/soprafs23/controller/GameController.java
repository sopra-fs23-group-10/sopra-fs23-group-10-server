package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GameDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
public class GameController {

    private final GameService gameService;
    private final UserService userService;

    private final HashMap<Long, Game> games = new HashMap();

    GameController(GameService gameService, UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
    }

    @PostMapping("/game/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserGetDTO createUser(@PathVariable long userId,  @RequestBody GameDTO gameDTO, @RequestHeader("token") String token) {
        GameDTO userInput = DTOMapper.INSTANCE.convertGamePostDTOtoEntity(gameDTO);
        User invitedUser = userService.searchUserById(userId);
        User invitingUser = userService.searchUserById(userInput.getInvitedUserId());



        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(invitedUser);
    }





}
