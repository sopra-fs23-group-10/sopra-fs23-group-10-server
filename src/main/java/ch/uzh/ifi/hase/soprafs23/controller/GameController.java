package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GameDTO;
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
    private long index = 0;

    GameController(GameService gameService, UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
    }

    @PostMapping("/game/creation")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public GameDTO createUser(@RequestBody GameDTO gameDTO, @RequestHeader("token") String token) {
        User invitedUser = userService.searchUserById(gameDTO.getInvitingUserId());
        User invitingUser = userService.searchUserById(gameDTO.getInvitedUserId());
        Game game = new Game(this.index, invitingUser.getId(), invitedUser.getId(), gameDTO.getQuizType(), gameDTO.getModeType());
        this.games.put(game.getGameId(), game);
        return DTOMapper.INSTANCE.convertGameEntityToPostDTO(game);
    }





}
