package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.ModeType;
import ch.uzh.ifi.hase.soprafs23.constant.QuizType;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.UserResultTuple;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GameDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.QuestionDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import ch.uzh.ifi.hase.soprafs23.service.WebSocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    private final UserService userService;
    private final GameService gameService;
    private final WebSocketService webSocketService;
    Logger log = LoggerFactory.getLogger(WebSocketController.class);

    public WebSocketController(UserService userService, GameService gameService, WebSocketService webSocketService) {
        this.userService = userService;
        this.gameService = gameService;
        this.webSocketService = webSocketService;
    }

    @MessageMapping("/invitation/{userId}")
    public void inviteUser(@DestinationVariable long invitedUserId, GameDTO gameDTO) {
        this.webSocketService.sendMessageToClients("/invitations/" + invitedUserId, gameDTO);
    }

    @MessageMapping("/game/result/{gameId}")
    public void resultToUser(@DestinationVariable long GameId, GameDTO gameDTO) {
        Game currentGame = DTOMapper.INSTANCE.convertGamePostDTOtoEntity(gameDTO);

        UserResultTuple gameResults = currentGame.getResults();
        User invitedUser = userService.searchUserById(gameDTO.getInvitedUserId());
        User invitingUser = userService.searchUserById(gameDTO.getInvitingUserId());

        //TODO: Points in User DB should only be updated when a game ends completely, not between rounds
        invitingUser.setPoints(invitingUser.getPoints() + gameResults.getInvitingPlayerResult());
        invitedUser.setPoints(invitedUser.getPoints() + gameResults.getInvitedPlayerResult());

        this.webSocketService.updatePoints(invitedUser.getPoints(),invitedUser.getId());
        this.webSocketService.updatePoints(invitingUser.getPoints(),invitingUser.getId());

        this.webSocketService.sendMessageToClients("/game/result/" + GameId, gameDTO);
    }
}