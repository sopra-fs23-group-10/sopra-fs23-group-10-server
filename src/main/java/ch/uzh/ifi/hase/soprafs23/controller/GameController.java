package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.Category;
import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.rest.dto.AnswerDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GameDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.QuestionDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserResultTupleDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameControllerService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
public class GameController {

    private final GameControllerService gameControllerService;
    private final UserService userService;
    private final WebSocketController webSocketController;

    GameController(GameControllerService gameControllerService, UserService userService, WebSocketController webSocketController) {
        this.gameControllerService = gameControllerService;
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

        if(invitingUser.getStatus() != UserStatus.ONLINE ||invitedUser.getStatus() != UserStatus.ONLINE){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The invited user or you is not online");
        }

        Game game = gameControllerService.createGame(invitingUser.getId(), invitedUser.getId(), requestedGameDTO.getQuizType(), requestedGameDTO.getModeType());

        GameDTO createdGameDTO = DTOMapper.INSTANCE.convertGameEntityToPostDTO(game);

        webSocketController.inviteUser(game.getInvitedUserId(), createdGameDTO);
        return createdGameDTO;
    }


    @PostMapping("/game/invitation/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Map<Long, Boolean> respondInvitation(@PathVariable Long gameId, @RequestBody Boolean response, @RequestHeader("token") String token){
        userService.verifyToken(token);

        Game game = gameControllerService.searchGame(gameId);

        if(!response) {
            userService.setOnline(game.getInvitedUserId());
            userService.setOnline(game.getInvitingUserId());
            gameControllerService.removeGame(gameId);
        }

        Map<Long, Boolean> answer = Collections.singletonMap(gameId, response);
        webSocketController.sendInvitationRespond(game.getInvitedUserId(), answer);
        webSocketController.sendInvitationRespond(game.getInvitingUserId(), answer);
        return answer;
    }

    @GetMapping("/game/topics/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Map<String, List<Category>> getTopicSelection(@PathVariable Long gameId, @RequestHeader("token") String token) {
        User requestingUser = userService.verifyToken(token);
        return gameControllerService.getRandomTopics(gameId, requestingUser.getId());
    }

    @GetMapping("/game/topics/all")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Map<String, List<Category>> getAllTopics(@RequestHeader("token") String token) {
        userService.verifyToken(token);
        return gameControllerService.getAllTopics();
    }

    @PostMapping("/game/topics")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public QuestionDTO createQuestion(@RequestBody QuestionDTO questionDTO, @RequestHeader("token") String token){
        userService.verifyToken(token);
        QuestionDTO questionDTOReturn = DTOMapper.INSTANCE.convertQuestionEntityToDTO(gameControllerService.getQuestion(questionDTO.getCategory(), questionDTO.getGameId()));
        webSocketController.questionToUsers(questionDTO.getGameId(),questionDTOReturn);
        return questionDTOReturn;
    }

    @PutMapping("/game/question/{gameId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void answerQuestion(@PathVariable long gameId, @RequestBody AnswerDTO answerDTO, @RequestHeader("token") String token) {
        userService.verifyToken(token);
        Answer answer = DTOMapper.INSTANCE.convertUserAnswerDTOtoEntity(answerDTO);
        gameControllerService.answerQuestion(gameId, answer);
    }

    @GetMapping("game/online/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Map<String, Boolean> allUsersConnected(@PathVariable long gameId, @RequestHeader("token") String token){
        userService.verifyToken(token);
        return gameControllerService.allUsersConnected(gameId);
    }

    @GetMapping("/game/finish/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserResultTupleDTO> finishGame(@PathVariable long gameId, @RequestHeader("token") String token) {
        userService.verifyToken(token);
        List<UserResultTupleDTO> userResultTupleDTOList = gameControllerService.getEndResult(gameId);

        webSocketController.resultToUser(gameId, userResultTupleDTOList);
        return userResultTupleDTOList;
    }

    @DeleteMapping("/game/finish/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    public GameDTO deleteFinishedGame(@PathVariable long gameId, @RequestHeader("token") String token) {
        userService.verifyToken(token);
        Game game = gameControllerService.searchGame(gameId);
        gameControllerService.removeGame(gameId);
        return DTOMapper.INSTANCE.convertGameEntityToPostDTO(game);
    }

    @GetMapping("/game/intermediate/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserResultTupleDTO> intermediateGame(@PathVariable long gameId, @RequestHeader("token") String token) {
        userService.verifyToken(token);

        List<UserResultTupleDTO> userResultTupleDTOList = gameControllerService.intermediateResults(gameId);

        webSocketController.resultToUser(gameId, userResultTupleDTOList);
        return userResultTupleDTOList;
    }

    @GetMapping("/games/{gameId}/users")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserResultTupleDTO getAllUsers(@PathVariable long gameId, @RequestHeader("token") String token) {
        userService.verifyToken(token);
        return gameControllerService.getAllUsersOfGame(gameId);
    }

    @DeleteMapping("/games/{gameId}/deletions")
    @ResponseStatus(HttpStatus.OK)
    public GameDTO deleteGame(@PathVariable long gameId, @RequestHeader("token") String token) {
        userService.verifyToken(token);
        Game deletedGame = gameControllerService.searchGame(gameId);
        gameControllerService.removeGame(gameId);
        webSocketController.informUsersGameDeleted(gameId);
        return DTOMapper.INSTANCE.convertGameEntityToPostDTO(deletedGame);
    }
}
