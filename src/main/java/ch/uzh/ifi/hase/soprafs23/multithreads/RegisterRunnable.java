package ch.uzh.ifi.hase.soprafs23.multithreads;

import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterRunnable implements Runnable{
    private final UserService userService;
    private final Long userId;
    private final Logger log = LoggerFactory.getLogger(RegisterRunnable.class);

    public RegisterRunnable(Long userId, UserService userService){
        this.userId = userId;
        this.userService = userService;
    }

    @Override
    public void run() {
        userService.searchUserById(userId);
        userService.setOnline(userId);
        log.info("User with userID: {} has logged IN%n",userId);
    }
}
