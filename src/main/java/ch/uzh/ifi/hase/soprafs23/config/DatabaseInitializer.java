package ch.uzh.ifi.hase.soprafs23.config;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);
    
    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) {
        User user = new User();
        user.setUsername("singletonUser");
        user.setEmail("singleton@singleton.ch");
        user.setPassword("singletonPassword");
        user.setToken(UUID.randomUUID().toString());
        user.setStatus(UserStatus.ONLINE);
        user.setProfilePicture(user.getUsername());
        user.setPoints(0L);
        user.setId(0);
        try {
            userRepository.save(user);
            userRepository.flush();
            log.debug("SingletonUser inserted into DB");
        } catch (Exception ex) {
            log.debug("Exception was raised during set up of default user: " + ex.getMessage());
        }
    }
}
