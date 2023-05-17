package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.Category;
import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.rest.dto.AnswerDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GameDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.QuestionDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserResultTupleDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.AnswerService;
import ch.uzh.ifi.hase.soprafs23.service.GameControllerService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
public class GameController {

  private final GameControllerService gameControllerService;
  private final UserService userService;
  private final WebSocketController webSocketController;
  private final Logger log = LoggerFactory.getLogger(GameController.class);
  private static final String NO_GAME_FOUND = "No game with ID: {} found";

  GameController(GameControllerService gameControllerService, UserService userService, WebSocketController webSocketController) {
    this.gameControllerService = gameControllerService;
    this.userService = userService;
    this.webSocketController = webSocketController;
  }

  @PostMapping("/games")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public GameDTO createGame(@RequestBody GameDTO requestedGameDTO, @RequestHeader("token") String token) {
    userService.verifyToken(token);
    User invitedUser = userService.searchUserById(requestedGameDTO.getInvitedUserId());
    User invitingUser = userService.searchUserById(requestedGameDTO.getInvitingUserId());
    userService.setOnline(invitingUser.getId());

    if(invitingUser.getStatus() != UserStatus.ONLINE || (invitedUser.getId() != 0 && invitedUser.getStatus() != UserStatus.ONLINE)){
        throw new ResponseStatusException(HttpStatus.CONFLICT, "One of the users is not online.");
    }

    Game game = gameControllerService.createGame(invitingUser.getId(), invitedUser.getId(), requestedGameDTO.getQuizType(), requestedGameDTO.getModeType());

    GameDTO createdGameDTO = DTOMapper.INSTANCE.convertGameEntityToPostDTO(game);
    if (game.getInvitedUserId() != 0){
        webSocketController.inviteUser(game.getInvitedUserId(), createdGameDTO);
    }
    return createdGameDTO;
  }

  @PostMapping("/games/{gameId}/invitation")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public Map<Long, Boolean> respondInvitation(@PathVariable Long gameId, @RequestBody Boolean response, @RequestHeader("token") String token){
      userService.verifyToken(token);

      Game game = gameControllerService.searchGame(gameId);

      if(Boolean.FALSE.equals(response)) {
          try {
              gameControllerService.setInGamePlayersToOnline(gameId);
          } catch (ResponseStatusException e) {
              log.info(NO_GAME_FOUND,gameId);
          }
      }

      Map<Long, Boolean> answer = Collections.singletonMap(gameId, response);
      webSocketController.sendInvitationRespond(game.getInvitedUserId(), answer);
      webSocketController.sendInvitationRespond(game.getInvitingUserId(), answer);
      return answer;
  }

  @GetMapping("/games/{gameId}/topics")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public Map<String, List<Category>> getTopicSelection(@PathVariable Long gameId, @RequestHeader("token") String token) {
      userService.verifyToken(token);
      return gameControllerService.getRandomTopics(gameId);
  }

  @GetMapping("/games/topics/all")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public Map<String, List<Category>> getAllTopics(@RequestHeader("token") String token) {
      userService.verifyToken(token);
      return gameControllerService.getAllTopics();
  }

  @PostMapping("/games/topics")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public QuestionDTO createQuestion(@RequestBody QuestionDTO questionDTO, @RequestHeader("token") String token){
      userService.verifyToken(token);
      Question question = gameControllerService.getQuestion(questionDTO.getCategory(), questionDTO.getGameId());
      QuestionDTO questionDTOReturn = DTOMapper.INSTANCE.convertQuestionEntityToDTO(question);
      webSocketController.questionToUsers(questionDTO.getGameId(),questionDTOReturn);
      return questionDTOReturn;
  }

  @PostMapping("/games/images")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public QuestionDTO createImageQuestion(@RequestBody QuestionDTO questionDTO, @RequestHeader("token") String token){
    userService.verifyToken(token);
    Question question = gameControllerService.getImageQuestion(questionDTO.getGameId());
    QuestionDTO questionDTOReturn = DTOMapper.INSTANCE.convertQuestionEntityToDTO(question);
    webSocketController.questionToUsers(questionDTO.getGameId(),questionDTOReturn);
    return questionDTOReturn;
  }

  @PutMapping("/games/{gameId}/question")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public Map<String, String> answerQuestion(@PathVariable long gameId, @RequestBody AnswerDTO answerDTO, @RequestHeader("token") String token) {
      userService.verifyToken(token);
      Answer answer = DTOMapper.INSTANCE.convertUserAnswerDTOtoEntity(answerDTO);
      String correctAnswer = gameControllerService.answerQuestion(answer);
      if (gameControllerService.bothAnswered(gameId, answer.getQuestionId())){
        webSocketController.informUsersBothAnswered(gameId);
      }
      return Collections.singletonMap("correctAnswer", correctAnswer);
  }

  //TODO: Is this even called in frontend?
  @GetMapping("/games/{gameId}/online")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public Map<String, Boolean> allUsersConnected(@PathVariable long gameId, @RequestHeader("token") String token){
      userService.verifyToken(token);
      return gameControllerService.allUsersConnected(gameId);
  }

  @GetMapping("/games/{gameId}/finish")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<UserResultTupleDTO> finishGame(@PathVariable long gameId, @RequestHeader("token") String token) {
      userService.verifyToken(token);
      List<UserResultTupleDTO> userResultTupleDTOList = gameControllerService.getEndResult(gameId);

      webSocketController.resultToUser(gameId, userResultTupleDTOList);
      try {
          gameControllerService.setInGamePlayersToOnline(gameId);
      } catch (ResponseStatusException e) {
          log.info(NO_GAME_FOUND, gameId);
      }
      return userResultTupleDTOList;
  }


  //TODO: Not required anymore, contained in finishGame
  @DeleteMapping("/games/{gameId}/finish")
  @ResponseStatus(HttpStatus.OK)
  public void terminateFinishedGame(@PathVariable long gameId, @RequestHeader("token") String token) {
      userService.verifyToken(token);
      try {
          gameControllerService.setInGamePlayersToOnline(gameId);
      } catch (ResponseStatusException e) {
          log.info(NO_GAME_FOUND,gameId);
      }
  }

  @GetMapping("/games/{gameId}/intermediate")
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

  @PostMapping("/games/{gameId}/termination")
  @ResponseStatus(HttpStatus.OK)
  public GameDTO terminateGame(@PathVariable long gameId, @RequestHeader("token") String token) {
      userService.verifyToken(token);
      Game deletedGame = gameControllerService.searchGame(gameId);
      gameControllerService.setInGamePlayersToOnline(gameId);
      webSocketController.informUsersGameDeleted(gameId);
      return DTOMapper.INSTANCE.convertGameEntityToPostDTO(deletedGame);
  }
}
