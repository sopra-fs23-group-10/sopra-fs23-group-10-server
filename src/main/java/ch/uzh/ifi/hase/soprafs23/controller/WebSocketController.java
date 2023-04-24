package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.WebSockets.WebSocketSessionRegistry;
import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.multithreads.RegisterRunnable;
import ch.uzh.ifi.hase.soprafs23.multithreads.UnregisterRunnable;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GameDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.QuestionDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserResultTupleDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameControllerService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import ch.uzh.ifi.hase.soprafs23.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;


import java.util.List;
import java.util.Map;

@Controller
public class WebSocketController {
    private final UserService userService;

    private final GameControllerService gameControllerService;

    @Autowired
    private WebSocketSessionRegistry sessionRegistry;
    private final WebSocketService webSocketService;

    public WebSocketController(UserService userService, GameControllerService gameControllerService, WebSocketService webSocketService) {
        this.userService = userService;
        this.gameControllerService = gameControllerService;
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
    public void resultToUser(long gameId, UserResultTupleDTO userResultTupleDTOList) {
        this.webSocketService.sendMessageToClients("/game/result/" + gameId, userResultTupleDTOList);
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
    public void register(@Payload String userId) {
        try {
            Thread.sleep(10);
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (userId == null || userId.isEmpty()) {return;}
        Long id = Long.parseLong(userId);
        RegisterRunnable registerRunnable = new RegisterRunnable(id,userService,gameControllerService);
        Thread thread = new Thread(registerRunnable);
        thread.start();
        /*
        Long gameId = gameControllerService.getGameIdOfUser(userId);
        if(gameControllerService.gameExists(gameId)){
            userService.setOnline(userId);
            System.out.printf("User with userID: %s has logged IN%n", userId);
        }
         */
    }

    @MessageMapping("/unregister")
    public void unregister(@Payload String userId) {
        if (userId == null || userId.isEmpty()) {return;}
        Long id = Long.parseLong(userId);
        UnregisterRunnable unregisterRunnable = new UnregisterRunnable(id,userService,gameControllerService);
        Thread thread = new Thread(unregisterRunnable);
        thread.start();
        /*
        User user = userService.searchUserById(id);
        userService.setOffline(user, id);
        System.out.printf("User with userID: %s has logged OUT%n", userId);
        try {
            Thread.sleep(2000);
        }
        catch (InterruptedException e) {}
        User userUpdated = userService.searchUserById(id);
        if(userUpdated.getStatus() == UserStatus.OFFLINE){
            System.out.println("Game is getting deleted...");
            this.deathSwitch(id);
        }*/
    }
}