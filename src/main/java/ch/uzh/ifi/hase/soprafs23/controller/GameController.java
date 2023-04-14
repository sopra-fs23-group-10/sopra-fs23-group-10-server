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
    private final HashMap<Long, Game> games = new HashMap<>();
    private long index = 0;

    GameController(GameService gameService, UserService userService, WebSocketController webSocketController) {
        this.gameService = gameService;
        this.userService = userService;
        this.webSocketController = webSocketController;
    }

    protected HashMap<Long, Game> getGames() {
        return games;
    }

    @PostMapping("/game/creation")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public GameDTO createGame(@RequestBody GameDTO requestedGameDTO, @RequestHeader("token") String token) {
        User invitedUser = userService.searchUserById(requestedGameDTO.getInvitedUserId());
        User invitingUser = userService.searchUserById(requestedGameDTO.getInvitingUserId());
        Game game = new Game(this.index, invitingUser.getId(), invitedUser.getId(), requestedGameDTO.getQuizType(), requestedGameDTO.getModeType());
        this.index ++;
        this.games.put(game.getId(), game);
        GameDTO createdGameDTO = DTOMapper.INSTANCE.convertGameEntityToPostDTO(game);
        webSocketController.inviteUser(game.getInvitedUserId(), createdGameDTO);
        return createdGameDTO;
    }


    @PostMapping("/game/invitation/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Map<Long, Boolean> respondInvitation(@PathVariable Long gameId, @RequestBody Boolean response, @RequestHeader("token") String token){
        userService.verifyToken(token);
        Game game = games.get(gameId);
        if(game==null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game does not exist");
        }

        if(!response){
            games.remove(gameId);
        }

        Map answer = Collections.singletonMap(gameId,response);
        webSocketController.sendInviationRespond(game.getInvitedUserId(), answer);
        webSocketController.sendInviationRespond(game.getInvitingUserId(), answer);
        return answer;
    }

    @GetMapping("/game/topics/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Map<String, List<Category>> getTopicSelection(@PathVariable Long gameId, @RequestHeader("token") String token) {
        User requestingUser = userService.verifyToken(token);

        Game game = games.get(gameId);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game does not exist");
        }

        Long currentPlayerId = game.getCurrentPlayer();
        if (!requestingUser.getId().equals(currentPlayerId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "This user cannot request topics at this point.");
        }

        List<Category> randomTopics = new ArrayList<>(Arrays.asList(Category.values()));
        while (randomTopics.size() > 3) {
            randomTopics.remove((int) ((Math.random() * randomTopics.size())));
        }

        game.changeCurrentPlayer();

        return Collections.singletonMap("topics", randomTopics);
    }

    @GetMapping("/game/topics/all")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Map<String, List<Category>> getAllTopics(@RequestHeader("token") String token) {
        userService.verifyToken(token);

        List<Category> allTopics = new ArrayList<>(Arrays.asList(Category.values()));

        return Collections.singletonMap("topics", allTopics);
    }


    @PostMapping("/game/topics")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public QuestionDTO createQuestion(@RequestBody QuestionDTO questionDTO, @RequestHeader("token") String token){
        Game game = games.get(questionDTO.getGameId());
        userService.searchUserById(game.getInvitedUserId());
        userService.searchUserById(game.getInvitingUserId());
        Question question = gameService.getQuestion(questionDTO.getCategory());
        game.addQuestion(question);
        return DTOMapper.INSTANCE.convertQuestionEntityToDTO(question);
    }


    @PutMapping("/game/question/{gameId}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserResultTuple answerQuestion(@PathVariable long gameId, @RequestBody UserAnswerDTO userAnswerDTO, @RequestHeader("token") String token) {
        userService.verifyToken(token);
        UserAnswerTuple userAnswerTuple = DTOMapper.INSTANCE.convertUserAnswerDTOtoEntity(userAnswerDTO);
        Game currentGame = games.get(gameId);
        this.checkGame(currentGame);
        UserResultTuple userResultTuple = currentGame.getResults();
        UserResultTupleDTO userResultTupleDTO = DTOMapper.INSTANCE.convertUserResultTupleEntitytoDTO(userResultTuple);
        if (currentGame.completelyAnswered()) {
            webSocketController.resultToUser(gameId, userResultTupleDTO);
        }
        else {
            currentGame.addAnswer(userAnswerTuple);
        }
        return userResultTuple;
    }

    @GetMapping("game/online/{gameId}")
    @ResponseStatus
    public Map<String, Boolean> allUsersConnected(@PathVariable long gameId, @RequestHeader("token") String token){
        Game game = games.get(gameId);
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
        Game currentGame = games.get(gameId);
        this.checkGame(currentGame);

        UserResultTuple userResultTuple = currentGame.getResults();
        userService.updatePoints(userResultTuple);
        long invitingUserPoints = userService.getPoints(userResultTuple.getInvitingPlayerId());
        long invitedUserPoints = userService.getPoints(userResultTuple.getInvitedPlayerId());

        UserResultTuple finalResult = new UserResultTuple(gameId,userResultTuple.getInvitingPlayerId(),userResultTuple.getInvitedPlayerId());
        finalResult.setInvitingPlayerResult(invitingUserPoints);
        finalResult.setInvitedPlayerResult(invitedUserPoints);

        UserResultTupleDTO userResultTupleDTO = DTOMapper.INSTANCE.convertUserResultTupleEntitytoDTO(finalResult);

        webSocketController.resultToUser(gameId, userResultTupleDTO);

        return userResultTupleDTO;
    }

    @PutMapping("/game/intermediate/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserResultTupleDTO intermediateGame(@PathVariable long gameId, @RequestHeader("token") String token) {
        userService.verifyToken(token);
        Game currentGame = games.get(gameId);
        this.checkGame(currentGame);
        UserResultTuple userResultTuple = currentGame.getResults();

        UserResultTupleDTO userResultTupleDTO = DTOMapper.INSTANCE.convertUserResultTupleEntitytoDTO(userResultTuple);

        webSocketController.resultToUser(gameId, userResultTupleDTO);

        return userResultTupleDTO;
    }

    private void checkGame(Game currentGame){
        if (currentGame == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game with corresponding gameID cannot be found.");
        }
    }
}
