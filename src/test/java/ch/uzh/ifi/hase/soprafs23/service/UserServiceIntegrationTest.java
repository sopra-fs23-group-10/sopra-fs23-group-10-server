package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@SpringBootTest
public class UserServiceIntegrationTest {

  @Qualifier("userRepository")
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserService userService;

  @BeforeEach
  public void setup() {
    userRepository.deleteAll();
  }

    @Test
    public void createUser_validInputs_success() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");

        // when
        User createdUser = userService.createUser(testUser);

        // then
        assertEquals(testUser.getId(), createdUser.getId());
        assertEquals(testUser.getUsername(), createdUser.getUsername());
        assertEquals(testUser.getPassword(), createdUser.getPassword());
        assertNotNull(createdUser.getToken());
        assertEquals(UserStatus.ONLINE, createdUser.getStatus());
    }

    @Test
    public void createUser_duplicateUsername_throwsException() {
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        User createdUser = userService.createUser(testUser);

        // attempt to create second user with same username
        User testUser2 = new User();
        testUser2.setUsername("testUsername");
        testUser2.setPassword("testPassword");

        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser2));
    }

    @Test
    public void verifyToken_validToken_success() {
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("password");

        userService.createUser(testUser);

        assertEquals(userService.verifyToken(testUser.getToken()).getToken(), testUser.getToken());
    }

    @Test
    public void verifyToken_invalidToken_ThrowsUnauthorized() {
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("password");

        userService.createUser(testUser);

        assertThrows(ResponseStatusException.class, () -> userService.verifyToken(testUser.getToken() + "something"));
    }

    @Test
    public void verifyTokenWithId_bothValid_success() {
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("password");

        userService.createUser(testUser);
        User found = userService.verifyTokenWithId(testUser.getToken(), testUser.getId());

        assertEquals(testUser.getUsername(), found.getUsername());
        assertEquals(testUser.getPassword(), found.getPassword());
        assertEquals(testUser.getId(), found.getId());
        assertEquals(testUser.getToken(), found.getToken());
        assertEquals(testUser.getCreationDate(), found.getCreationDate());
        assertEquals(testUser.getStatus(), found.getStatus());
    }

    @Test
    public void verifyTokenWithId_invalidToken_throwsUnauthorized() {
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("password");

        userService.createUser(testUser);

        assertThrows(ResponseStatusException.class, () -> userService.verifyTokenWithId(testUser.getToken() + "something", testUser.getId()));
    }

    @Test
    public void verifyTokenWithId_noMatchingId_throwsUnauthorized() {
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("password");

        userService.createUser(testUser);

        assertThrows(ResponseStatusException.class, () -> userService.verifyTokenWithId(testUser.getToken(), -1L));
    }

    @Test
    public void checkDoLogout_success() {
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("password");

        userService.createUser(testUser);

        assertEquals(UserStatus.ONLINE, testUser.getStatus());

        userService.setOffline(testUser, testUser.getId());

        assertEquals(UserStatus.OFFLINE, testUser.getStatus());
    }

    @Test
    public void checkDoLogout_noMatchingId_failure() {
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("password");

        userService.createUser(testUser);

        assertEquals(UserStatus.ONLINE, testUser.getStatus());

        assertThrows(ResponseStatusException.class, () -> userService.setOffline(testUser, -1L));
    }

    @Test
    public void changeUsernameBirthday_allValid_success() {
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        userService.createUser(testUser);

        User testUser2 = new User();
        testUser2.setUsername("testUsername");
        testUser2.setBirthdayDate(Date.from(Instant.parse("2023-03-01T22:22:22.999+00:00")));

        User changedUser = userService.changeUsernameBirthday(testUser.getId(), testUser2);

        assertEquals(changedUser.getUsername(), testUser2.getUsername());
        assertEquals(changedUser.getBirthdayDate(), testUser2.getBirthdayDate());
        assertEquals(changedUser.getId(), testUser.getId());
        assertEquals(changedUser.getToken(), testUser.getToken());
    }

    @Test
    public void changeUsernameBirthday_duplicateUsername_success() {
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        userService.createUser(testUser);

        User testUserConflict = new User();
        testUserConflict.setUsername("conflictingUsername");
        testUserConflict.setPassword("testPassword");
        userService.createUser(testUserConflict);

        User testUser2 = new User();
        testUser2.setUsername(testUserConflict.getUsername());
        testUser2.setBirthdayDate(Date.from(Instant.parse("2023-03-01T22:22:22.999+00:00")));

        assertThrows(ResponseStatusException.class, () -> userService.changeUsernameBirthday(testUser.getId(), testUser2));
    }
}
