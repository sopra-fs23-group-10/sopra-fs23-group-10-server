package ch.uzh.ifi.hase.soprafs23.multithreads;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.controller.WebSocketController;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.service.GameControllerService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;

public class RegisterRunnable implements Runnable{
    private final UserService userService;
    private final GameControllerService gameControllerService;
    private Long userId;

    public RegisterRunnable(Long userId, UserService userService,GameControllerService gameControllerService){
        this.userId = userId;
        this.userService = userService;
        this.gameControllerService = gameControllerService;
    }

    @Override
    public void run() {
        Long gameId = gameControllerService.getGameIdOfUser(userId);
        if(gameControllerService.gameExists(gameId)){
            userService.setOnline(userId);
            System.out.printf("User with userID: %s has logged IN%n", userId);
        }
    }
}
