package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.Category;
import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GameDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.QuestionDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserAnswerDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserResultTupleDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
public class GameController {

    private final GameService gameService;
    private final UserService userService;
    private final WebSocketController webSocketController;

    GameController(GameService gameService, UserService userService, WebSocketController webSocketController) {
        this.gameService = gameService;
        this.userService = userService;
        this.webSocketController = webSocketController;
    }

    @PostMapping("/game/creation")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public GameDTO createGame(@RequestBody GameDTO requestedGameDTO, @RequestHeader("token") String token) {
        userService.verifyToken(token);

        User invitedUser = userService.searchUserById(requestedGameDTO.getInvitedUserId());
        User invitingUser = userService.searchUserById(requestedGameDTO.getInvitingUserId());

        Game game = gameService.createGame(invitingUser.getId(), invitedUser.getId(), requestedGameDTO.getQuizType(), requestedGameDTO.getModeType());

        GameDTO createdGameDTO = DTOMapper.INSTANCE.convertGameEntityToPostDTO(game);

        webSocketController.inviteUser(game.getInvitedUserId(), createdGameDTO);
        return createdGameDTO;
    }


    @PostMapping("/game/invitation/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Map<Long, Boolean> respondInvitation(@PathVariable Long gameId, @RequestBody Boolean response, @RequestHeader("token") String token){
        userService.verifyToken(token);

        Game game = gameService.getGame(gameId);

        if(game==null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game does not exist");
        }

        if(!response){
            gameService.removeGame(gameId);
        }

        Map<Long, Boolean> answer = Collections.singletonMap(gameId,response);
        webSocketController.sendInviationRespond(game.getInvitedUserId(), answer);
        webSocketController.sendInviationRespond(game.getInvitingUserId(), answer);
        return answer;
    }

    @GetMapping("/game/topics/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Map<String, List<Category>> getTopicSelection(@PathVariable Long gameId, @RequestHeader("token") String token) {
        User requestingUser = userService.verifyToken(token);
        return gameService.getRandomTopics(gameId, requestingUser.getId());
    }

    @GetMapping("/game/topics/all")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Map<String, List<Category>> getAllTopics(@RequestHeader("token") String token) {
        userService.verifyToken(token);
        return gameService.getAllTopics();
    }


    @PostMapping("/game/topics")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public QuestionDTO createQuestion(@RequestBody QuestionDTO questionDTO, @RequestHeader("token") String token){
        userService.verifyToken(token);
        return DTOMapper.INSTANCE.convertQuestionEntityToDTO(gameService.getQuestion(questionDTO.getCategory(), questionDTO.getGameId()));
    }


    @PutMapping("/game/question/{gameId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Boolean> answerQuestion(@PathVariable long gameId, @RequestBody UserAnswerDTO userAnswerDTO, @RequestHeader("token") String token) {
        userService.verifyToken(token);

        UserAnswerTuple userAnswerTuple = DTOMapper.INSTANCE.convertUserAnswerDTOtoEntity(userAnswerDTO);

        return gameService.answerQuestion(gameId, userAnswerTuple, webSocketController);
    }

    @GetMapping("game/online/{gameId}")
    @ResponseStatus
    public Map<String, Boolean> allUsersConnected(@PathVariable long gameId, @RequestHeader("token") String token){
        Game game = gameService.getGame(gameId);
        return Collections.singletonMap(
                "status",
                userService.searchUserById(game.getInvitingUserId()).getStatus().equals(UserStatus.ONLINE)
                && userService.searchUserById(game.getInvitedUserId()).getStatus().equals(UserStatus.ONLINE)
        );
    }

    @DeleteMapping("/game/finish/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserResultTupleDTO finishGame(@PathVariable long gameId, @RequestHeader("token") String token) {
        userService.verifyToken(token);

        UserResultTupleDTO userResultTupleDTO = DTOMapper.INSTANCE.convertUserResultTupleEntitytoDTO(gameService.finishGame(gameId));

        webSocketController.resultToUser(gameId, userResultTupleDTO);
        return userResultTupleDTO;
    }

    @GetMapping("/game/intermediate/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserResultTupleDTO intermediateGame(@PathVariable long gameId, @RequestHeader("token") String token) {
        userService.verifyToken(token);

        UserResultTupleDTO userResultTupleDTO = DTOMapper.INSTANCE.convertUserResultTupleEntitytoDTO(gameService.intermediateResults(gameId));

        webSocketController.resultToUser(gameId, userResultTupleDTO);
        return userResultTupleDTO;
    }
}
