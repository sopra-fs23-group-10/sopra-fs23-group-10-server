package ch.uzh.ifi.hase.soprafs23.multithreads;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.service.GameControllerService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ResponseStatusException;

public class UnregisterRunnable implements Runnable{
    private final UserService userService;
    private final GameControllerService gameControllerService;
    private final Long userId;
    private final Logger log = LoggerFactory.getLogger(UnregisterRunnable.class);

    public UnregisterRunnable(Long userId, UserService userService, GameControllerService gameControllerService){
        this.userId = userId;
        this.userService = userService;
        this.gameControllerService = gameControllerService;
    }

    @Override
    public void run() {
        userService.searchUserById(userId);
        userService.setOffline(userId);
        log.info("User with userID: {} has logged OUT%n",userId);
        try {
            Thread.sleep(2000);
            User updatedUser = userService.searchUserById(userId);
            if(updatedUser.getStatus()==UserStatus.OFFLINE){
                gameControllerService.deathSwitch(userId);
            }
        }
        catch (ResponseStatusException | InterruptedException r) {
            log.info("User was not in a game");
            Thread.currentThread().interrupt();
        }
    }
}
