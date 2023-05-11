package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.rest.dto.GameDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.QuestionDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserResultTupleDTO;
import ch.uzh.ifi.hase.soprafs23.service.WebSocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;


import java.util.List;
import java.util.Map;

@Controller
public class WebSocketController {

    private final Logger log = LoggerFactory.getLogger(WebSocketController.class);

    private static final String GAMES_URL = "/games/";

    private final WebSocketService webSocketService;

    public WebSocketController(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    public void inviteUser(long invitedUserId, GameDTO gameDTO) {
        this.webSocketService.sendMessageToClients("/invitations/" + invitedUserId, gameDTO);
    }

    public void sendInvitationRespond(Long userId, Map<Long,Boolean> map) {
        this.webSocketService.sendMessageToClients("/invitations/answer/" + userId,map);
    }

    public void resultToUser(long gameId, List<UserResultTupleDTO> userResultTupleDTOList) {
        this.webSocketService.sendMessageToClients("/game/result/" + gameId, userResultTupleDTOList);
    }

    public void questionToUsers(long gameId, QuestionDTO questionDTO) {
        log.info(GAMES_URL+"{}/questions",gameId);
        this.webSocketService.sendMessageToClients(GAMES_URL+gameId+"/questions", questionDTO);
    }

    public void informUsersGameDeleted(long gameId) {
        this.webSocketService.sendMessageToClients(GAMES_URL+gameId, "A user has quit the game");
    }
}
