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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@SpringBootTest
class UserServiceIntegrationTest {

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
    void createUser_validInputs_success() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        testUser.setEmail("email@email.com");

        // when
        User createdUser = userService.createUser(testUser);

        // then
        assertEquals(testUser.getId(), createdUser.getId());
        assertEquals(testUser.getUsername(), createdUser.getUsername());
        assertEquals(testUser.getPassword(), createdUser.getPassword());
        assertEquals(testUser.getEmail(), createdUser.getEmail());
        assertEquals(testUser.getUsername(), createdUser.getProfilePicture());
        assertNotNull(createdUser.getToken());
        assertEquals(UserStatus.ONLINE, createdUser.getStatus());
    }

    @Test
    void createUser_duplicateUsername_throwsException() {
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        testUser.setEmail("email@email.com");
        User createdUser = userService.createUser(testUser);

        // attempt to create second user with same username
        User testUser2 = new User();
        testUser2.setUsername("testUsername");
        testUser2.setPassword("testPassword");
        testUser2.setEmail("email@email.com");

        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser2));
    }

    @Test
    void verifyToken_validToken_success() {
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("password");
        testUser.setEmail("email@email.com");

        userService.createUser(testUser);

        assertEquals(userService.verifyToken(testUser.getToken()).getToken(), testUser.getToken());
    }

    @Test
    void verifyToken_invalidToken_ThrowsUnauthorized() {
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("password");
        testUser.setEmail("email@email.com");

        userService.createUser(testUser);

        assertThrows(ResponseStatusException.class, () -> userService.verifyToken(testUser.getToken() + "something"));
    }

    @Test
    void verifyTokenWithId_bothValid_success() {
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("password");
        testUser.setEmail("email@email.com");

        userService.createUser(testUser);
        User found = userService.verifyTokenWithId(testUser.getToken(), testUser.getId());

        assertEquals(testUser.getUsername(), found.getUsername());
        assertEquals(testUser.getPassword(), found.getPassword());
        assertEquals(testUser.getEmail(), found.getEmail());
        assertEquals(testUser.getId(), found.getId());
        assertEquals(testUser.getToken(), found.getToken());
        assertEquals(testUser.getUsername(), found.getProfilePicture());
        assertEquals(testUser.getStatus(), found.getStatus());
    }

    @Test
    void verifyTokenWithId_invalidToken_throwsUnauthorized() {
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("password");
        testUser.setEmail("email@email.com");

        userService.createUser(testUser);

        assertThrows(ResponseStatusException.class, () -> userService.verifyTokenWithId(testUser.getToken() + "something", testUser.getId()));
    }

    @Test
    void verifyTokenWithId_noMatchingId_throwsUnauthorized() {
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("password");
        testUser.setEmail("email@email.com");

        userService.createUser(testUser);

        assertThrows(ResponseStatusException.class, () -> userService.verifyTokenWithId(testUser.getToken(), -1L));
    }


    @Test
    void checkDoLogout_success() {
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("password");
        testUser.setEmail("email@email.com");

        userService.createUser(testUser);
        assertEquals(UserStatus.ONLINE, userService.searchUserById(testUser.getId()).getStatus());

        userService.setOffline(testUser.getId());
        assertEquals(UserStatus.OFFLINE, userService.searchUserById(testUser.getId()).getStatus());

        userService.setOnline(testUser.getId());
        assertEquals(UserStatus.ONLINE, userService.searchUserById(testUser.getId()).getStatus());
    }



    @Test
    void checkDoLogout_noMatchingId_failure() {
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("password");
        testUser.setEmail("email@email.com");

        userService.createUser(testUser);

        assertEquals(UserStatus.ONLINE, userService.searchUserById(testUser.getId()).getStatus());

        assertThrows(ResponseStatusException.class, () -> userService.setOffline(null));
    }

    @Test
    void changeUsername_allValid_success() {
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        testUser.setEmail("email@email.com");
        userService.createUser(testUser);

        User testUser2 = new User();
        testUser2.setUsername("changedUsername");

        User changedUser = userService.changeUsernameAndProfilePic(testUser.getId(), testUser2);

        assertEquals(changedUser.getUsername(), testUser2.getUsername());
        assertEquals(changedUser.getId(), testUser.getId());
        assertEquals(changedUser.getToken(), testUser.getToken());
    }

    @Test
    void changeUsernameBirthday_duplicateUsername_success() {
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        testUser.setEmail("email@email.com");
        userService.createUser(testUser);

        User testUserConflict = new User();
        testUserConflict.setUsername("conflictingUsername");
        testUserConflict.setPassword("testPassword");
        testUserConflict.setEmail("anotheremail@email.com");
        userService.createUser(testUserConflict);

        User testUser2 = new User();
        testUser2.setUsername(testUserConflict.getUsername());

        assertThrows(ResponseStatusException.class, () -> userService.changeUsernameAndProfilePic(testUser.getId(), testUser2));
    }
}
