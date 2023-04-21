package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.WebSockets.WebSocketSessionRegistry;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.UserResultTuple;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GameDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.QuestionDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserResultTupleDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import ch.uzh.ifi.hase.soprafs23.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
public class WebSocketController {
    private final UserService userService;

    private final GameService gameService;

    @Autowired
    private WebSocketSessionRegistry sessionRegistry;
    private final WebSocketService webSocketService;

    public WebSocketController(UserService userService,GameService gameService, WebSocketService webSocketService) {
        this.userService = userService;
        this.gameService = gameService;
        this.webSocketService = webSocketService;
    }

    @MessageMapping("/invitation/{userId}")
    public void inviteUser(long invitedUserId, GameDTO gameDTO) {
        this.webSocketService.sendMessageToClients("/invitations/" + invitedUserId, gameDTO);
    }

    @MessageMapping("/invitations/answer/{userId}")
    public void sendInviationRespond(Long userId, Map map) {
        this.webSocketService.sendMessageToClients("/invitations/answer/" + userId, map);
    }

    @MessageMapping("/game/intermediateResult/{gameId}")
    public void resultToUser(long gameId, GameDTO gameDTO) {
        Game currentGame = DTOMapper.INSTANCE.convertGamePostDTOtoEntity(gameDTO);
        List<UserResultTupleDTO> gameResults = currentGame.getResults();
        this.webSocketService.sendMessageToClients("/game/result/" + gameId, gameResults);
    }

    @MessageMapping("/game/finalResult/{gameId}")
    public void resultToUser(long gameId, List<UserResultTupleDTO> userResultTupleDTOList) {
        this.webSocketService.sendMessageToClients("/game/result/" + gameId, userResultTupleDTOList);
    }

    @MessageMapping("/games/{gameId}/question")
    public void questionToUsers(long gameId, QuestionDTO questionDTO) {
        System.out.println("/games/"+gameId+"/questions");
        this.webSocketService.sendMessageToClients("/games/"+gameId+"/questions", questionDTO);
    }

    @MessageMapping("/games/{gameId}/deletions")
    public void informUsersGameDeleted(long gameId) {
        this.webSocketService.sendMessageToClients("/games/"+gameId, "A user has quit the game");
    }

    @MessageMapping("/register")
    @Payload(required = false)
    public void register(@Payload String userId) {
        if (userId == null || userId.isEmpty()) {return;}
        Long id = Long.parseLong(userId);
        userService.setOnline(userService.searchUserById(id));
        System.out.printf("User with userID: %s has logged IN%n", userId);
    }

    @MessageMapping("/unregister")
    @Payload(required = false)
    public void unregister(@Payload String userId) {
        if (userId == null || userId.isEmpty()) {return;}
        Long id = Long.parseLong(userId);
        userService.setOffline(userService.searchUserById(id), id);
        System.out.printf("User with userID: %s has logged OUT%n", userId);
    }

    private void deathSwitch(Long userId){
        Long gameId = gameService.getGameIdOfUser(userId);
        if(gameId != -1L){
            gameService.removeGame(gameId);
            this.webSocketService.sendMessageToClients("/game/result/" + gameId, "Game was deleted since one of the players left the game");
        }
    }
}