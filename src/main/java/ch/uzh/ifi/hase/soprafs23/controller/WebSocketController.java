package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.UserResultTuple;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GameDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserResultTupleDTO;
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

    @MessageMapping("/game/intermediateResult/{gameId}")
    public void resultToUser(@DestinationVariable long GameId, GameDTO gameDTO) {
        Game currentGame = DTOMapper.INSTANCE.convertGamePostDTOtoEntity(gameDTO);

        UserResultTuple gameResults = currentGame.getResults();
        UserResultTupleDTO userResultTupleDTO = DTOMapper.INSTANCE.convertUserResultTupleEntitytoDTO(gameResults);

        this.webSocketService.sendMessageToClients("/game/result/" + GameId, userResultTupleDTO);
    }

    @MessageMapping("/game/finalResult/{gameId}")
    public void resultToUser(@DestinationVariable long GameId, UserResultTupleDTO userResultTupleDTO) {

        this.webSocketService.sendMessageToClients("/game/result/" + GameId, userResultTupleDTO);
    }
}