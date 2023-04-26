package ch.uzh.ifi.hase.soprafs23.multithreads;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.service.UserService;

public class RegisterRunnable implements Runnable{
    private final UserService userService;
    private final Long userId;

    public RegisterRunnable(Long userId, UserService userService){
        this.userId = userId;
        this.userService = userService;
    }

    @Override
    public void run() {
        User user = userService.searchUserById(userId);
        userService.setOnline(userId);
        System.out.printf("User with userID: %s has logged IN%n", userId);
    }
}
