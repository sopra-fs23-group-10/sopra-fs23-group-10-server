package ch.uzh.ifi.hase.soprafs23.multithreads;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.controller.WebSocketController;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.service.GameControllerService;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.springframework.web.server.ResponseStatusException;

public class UnregisterRunnable implements Runnable{
    private final UserService userService;
    private final GameControllerService gameControllerService;
    private Long userId;

    public UnregisterRunnable(Long userId, UserService userService, GameControllerService gameControllerService){
        this.userId = userId;
        this.userService = userService;
        this.gameControllerService = gameControllerService;
    }

    @Override
    public void run() {
        System.out.println("<<<<<<<<<< unregister >>>>>>>>>>>");
        User user = userService.searchUserById(userId);
        userService.setOffline(userId);
        System.out.printf("User with userID: %s has logged OUT%n", userId);
        try {
            System.out.println("Thread is going to sleep");
            Thread.sleep(2000);
            User updatedUser = userService.searchUserById(userId);
            System.out.println("After sleep user "+userId+" is found to be "+updatedUser.getStatus().toString());
            if(updatedUser.getStatus()==UserStatus.OFFLINE){
                gameControllerService.deathSwitch(userId);
                System.out.println("Game is deleted due to offline player");
            }
        }
        catch (ResponseStatusException | InterruptedException r) {
            System.out.println("User was not in a game");
        }
        System.out.println("<<<<<<<<<< end of unregister >>>>>>>>>>>");
    }
}
