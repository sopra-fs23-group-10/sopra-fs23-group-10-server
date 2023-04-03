package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Question;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.UserResultTuple;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GameDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.QuestionDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.QuestionResultDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import ch.uzh.ifi.hase.soprafs23.service.WebSocketService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;

@RestController
public class GameController {

    private final GameService gameService;
    private final UserService userService;
    private final WebSocketService webSocketService;
    private final WebSocketController webSocketController;
    private final HashMap<Long, Game> games = new HashMap<>();
    private long index = 0;

    GameController(GameService gameService, UserService userService, WebSocketService webSocketService, WebSocketController webSocketController) {
        this.gameService = gameService;
        this.userService = userService;
        this.webSocketService = webSocketService;
        this.webSocketController = webSocketController;
    }

    @PostMapping("/game/creation")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public GameDTO createGame(@RequestBody GameDTO gameDTO, @RequestHeader("token") String token) {
        User invitedUser = userService.searchUserById(gameDTO.getInvitedUserId());
        User invitingUser = userService.searchUserById(gameDTO.getInvitingUserId());
        Game game = new Game(this.index, invitingUser.getId(), invitedUser.getId(), gameDTO.getQuizType(), gameDTO.getModeType());
        this.index ++;
        this.games.put(game.getId(), game);
        gameDTO = DTOMapper.INSTANCE.convertGameEntityToPostDTO(game);
        webSocketController.inviteUser(game.getInvitedUserId(), gameDTO );
        return gameDTO;
    }


    @PostMapping("/game/topics")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public QuestionDTO createQuestion(@RequestBody QuestionDTO questionDTO, @RequestHeader("token") String token) throws IOException {
        Game game = games.get(questionDTO.getGameId());
        User invitedUser = userService.searchUserById(game.getInvitedUserId());
        User invitingUser = userService.searchUserById(game.getInvitingUserId());
        Question question = gameService.getQuestion(questionDTO.getCategory());
        game.addQuestion(question);
        QuestionDTO questionDTO1 = DTOMapper.INSTANCE.convertQuestionEntityToDTO(question);
        return questionDTO1;
    }

    @PostMapping("/game/question/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void answerQuestion(@PathVariable long gameId, @RequestBody QuestionResultDTO questionResultDTO) {
        QuestionResultDTO questionResultDTO1 = DTOMapper.INSTANCE.convertQuestionResultDTOtoEntitiy(questionResultDTO);
        Game currentGame = games.get(gameId);

        UserResultTuple gameResults = currentGame.getResults();
        User invitedUser = userService.searchUserById(gameResults.getInvitedPlayerId());
        User invitingUser = userService.searchUserById(gameResults.getInvitingPlayerId());

        invitedUser.setPoints(invitedUser.getPoints() + gameResults.getInvitedPlayerResult());
        invitingUser.setPoints(invitingUser.getPoints() + gameResults.getInvitingPlayerResult());

        this.webSocketService.updatePoints(invitedUser.getPoints(),invitedUser.getId());
        this.webSocketService.updatePoints(invitingUser.getPoints(),invitingUser.getId());

        this.webSocketService.sendMessageToClients("/game/result/" + gameId, questionResultDTO);
    }
}
