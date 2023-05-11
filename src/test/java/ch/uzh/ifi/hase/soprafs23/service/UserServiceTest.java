package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

  @Mock
  private MailSenderService mailSenderService;

  private User testUser;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);

    // given
    testUser = new User();
    testUser.setId(1L);
    testUser.setUsername("testUsername");
    testUser.setPassword("testPassword");
    testUser.setEmail("email@email.me");
    testUser.setToken("token");
    testUser.setStatus(UserStatus.ONLINE);
    testUser.setPoints(0L);

    // when -> any object is being saved in the userRepository -> return the dummy
    // testUser
    Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
  }

  @Test
  void verifyTokenWithId_nonMatchingId_throws() {
    User nonmatchingUser = new User();
    nonmatchingUser.setId(-112);
    nonmatchingUser.setToken("anotherToken");

    given(userRepository.findUserByToken(testUser.getToken())).willReturn(testUser);
    given(userRepository.findUserById(nonmatchingUser.getId())).willReturn(nonmatchingUser);

    assertThrows(ResponseStatusException.class, () -> userService.verifyTokenWithId(testUser.getToken(), nonmatchingUser.getId()));
  }

  @Test
  void getOnlineUsers_success() {
    User anotherUser = new User();
    anotherUser.setStatus(UserStatus.IN_GAME);
    anotherUser.setId(-112L);
    anotherUser.setToken("anotherToken");

    User thirdUser = new User();
    anotherUser.setStatus(UserStatus.OFFLINE);
    anotherUser.setId(-2L);
    anotherUser.setToken("hehehe_another_token");

    List<User> userList = new ArrayList<>();
    userList.add(testUser);
    userList.add(anotherUser);
    userList.add(thirdUser);

    given(userRepository.findAll()).willReturn(userList);

    List<User> returnedUsers = userService.getOnlineUsers();

    assertEquals(1, returnedUsers.size());
    assertEquals(testUser.getId(), returnedUsers.get(0).getId());
    assertEquals(testUser.getUsername(), returnedUsers.get(0).getUsername());
    assertEquals(testUser.getPassword(), returnedUsers.get(0).getPassword());
    assertEquals(testUser.getEmail(), returnedUsers.get(0).getEmail());
    assertEquals(testUser.getToken(), returnedUsers.get(0).getToken());
    assertEquals(testUser.getStatus(), returnedUsers.get(0).getStatus());
    assertEquals(testUser.getPoints(), returnedUsers.get(0).getPoints());
  }

  @Test
  void createUser_validInputs_success() {
    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    User createdUser = userService.createUser(testUser);

    // then
    verify(userRepository, times(1)).save(Mockito.any());

    assertEquals(testUser.getId(), createdUser.getId());
    assertEquals(testUser.getUsername(), createdUser.getUsername());
    assertEquals(testUser.getPassword(), createdUser.getPassword());
    assertEquals(testUser.getEmail(), createdUser.getEmail());
    assertNotNull(createdUser.getToken());
    assertEquals(UserStatus.ONLINE , createdUser.getStatus());
  }

  @Test
  void createUser_duplicateUsername_throwsException() {
    // given -> a first user has already been created
    userService.createUser(testUser);

    // when -> setup additional mocks for UserRepository
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
  }

  @Test
  void createUser_duplicateEmail_throwsException() {
    userService.createUser(testUser);

    Mockito.when(userRepository.findByEmail(Mockito.any())).thenReturn(testUser);

    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
  }

  @Test
  void checkLoginCredentials_success() {
    testUser.setStatus(UserStatus.OFFLINE);

    given(userRepository.findByUsername(testUser.getUsername())).willReturn(testUser);

    User returnedUser = userService.checkLoginCredentials(testUser.getUsername(), testUser.getPassword());

    assertNotNull(returnedUser);
    assertEquals(testUser.getId(), returnedUser.getId());
    assertEquals(testUser.getUsername(), returnedUser.getUsername());
    assertEquals(testUser.getPassword(), returnedUser.getPassword());
    assertEquals(testUser.getEmail(), returnedUser.getEmail());
    assertEquals(testUser.getToken(), returnedUser.getToken());
    assertEquals(testUser.getStatus(), returnedUser.getStatus());
    assertEquals(testUser.getPoints(), returnedUser.getPoints());
  }

  @Test
  void checkLoginCredentials_alreadyOnline_throwsException() {
    testUser.setStatus(UserStatus.ONLINE);

    given(userRepository.findByUsername(testUser.getUsername())).willReturn(testUser);

    Exception exception = assertThrows(ResponseStatusException.class, () -> userService.checkLoginCredentials(testUser.getUsername(), testUser.getPassword()));

    String expectedMessage = "User is already logged in.";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));

    String expectedStatusCode = "403 FORBIDDEN";
    assertTrue(actualMessage.contains(expectedStatusCode));
  }

  @Test
  void checkLoginCredentials_wrongPassword_throwsException() {
    testUser.setStatus(UserStatus.ONLINE);

    given(userRepository.findByUsername(testUser.getUsername())).willReturn(testUser);

    Exception exception = assertThrows(ResponseStatusException.class, () -> userService.checkLoginCredentials(testUser.getUsername(), "wroooooooong"));

    String expectedMessage = "Wrong password.";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));

    String expectedStatusCode = "409 NOT ACCEPTABLE";
    assertTrue(actualMessage.contains(expectedStatusCode));
  }

  @Test
  void checkLoginCredentials_wrongUsername_throwsException() {
    testUser.setStatus(UserStatus.ONLINE);

    given(userRepository.findByUsername(Mockito.any())).willReturn(null);

    Exception exception = assertThrows(ResponseStatusException.class, () -> userService.checkLoginCredentials("someNonExistingUser", testUser.getPassword()));

    String expectedMessage = "Username does not exist.";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));

    String expectedStatusCode = "404 NOT FOUND";
    assertTrue(actualMessage.contains(expectedStatusCode));
  }

    @Test
    void changeUsername_validInputs_success() {
        // when -> any object is being save in the userRepository -> return the dummy
        // createdUser
        User createdUser = userService.createUser(testUser);
        verify(userRepository, times(1)).save(Mockito.any());
        given(userRepository.findUserById(testUser.getId())).willReturn(createdUser);

        // then
        User changeUser = new User();
        changeUser.setUsername("changedUsername");

        User updatedUser = userService.changeUsernameAndProfilePic(1L, changeUser);

        assertEquals(testUser.getId(), updatedUser.getId());
        assertEquals(testUser.getUsername(), updatedUser.getUsername());
        assertEquals(testUser.getPassword(), updatedUser.getPassword());
        assertEquals(testUser.getEmail(), updatedUser.getEmail());
        assertNotNull(updatedUser.getToken());
        assertEquals(UserStatus.ONLINE , updatedUser.getStatus());
    }

    @Test
    void changeUsername_validInputs_throwsException404() {
        // when
        User changeUser = new User();
        changeUser.setUsername("changedUsername");

        // The outcome of the method call is asserted,
        // expecting the method to throw a ResponseStatusException
        Exception exception = assertThrows(ResponseStatusException.class,
                () -> userService.changeUsernameAndProfilePic(1L, changeUser));

        // Assert if the right message is thrown with the exception
        String expectedMessage = "User with specified userID does not exist.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        // Assert if the right status code is thrown with the exception
        String expectedStatusCode = "404 NOT FOUND";
        assertTrue(actualMessage.contains(expectedMessage));
    }

  @Test
  void setInGame_success() {
    given(userRepository.findUserById(testUser.getId())).willReturn(testUser);

    assertEquals(UserStatus.ONLINE, testUser.getStatus());
    userService.setInGame(testUser.getId());
    assertEquals(UserStatus.IN_GAME, testUser.getStatus());
  }

  @Test
  void setInGame_userNotFound_throws() {
    given(userRepository.findUserById(testUser.getId())).willReturn(null);

    assertThrows(ResponseStatusException.class, () -> userService.setInGame(testUser.getId()));
  }

  @Test
  void sendNewPassword_success() {
    given(userRepository.findByEmail(testUser.getEmail())).willReturn(testUser);
    given(userRepository.findUserById(testUser.getId())).willReturn(testUser);
    doNothing().when(mailSenderService).sendNewPassword(testUser);

    String oldPassword = testUser.getPassword();

    userService.sendNewPassword(testUser);

    assertNotEquals(oldPassword, testUser.getPassword());
  }

    @Test
    void updateRank_success(){

      User user1 = new User();
      User user2 = new User();
      User user3 = new User();
      user1.setPoints(3);
      user1.setId(1);
      user2.setPoints(2);
      user2.setId(2);
      user3.setPoints(1);
      user3.setId(3);
      List<User> users = Arrays.asList(user1, user2, user3);

      given(userRepository.findAll()).willReturn(users);

      long nextLowestRank = userService.calculateRanks();

      verify(userRepository, times(3)).save(Mockito.any());
      verify(userRepository, times(1)).flush();

      assertEquals(4L, nextLowestRank);
      assertEquals(1L, user1.getRank());
      assertEquals(2L, user2.getRank());
      assertEquals(3L, user3.getRank());
    }

  @Test
  void setRandomPassword_success() {
    given(userRepository.findUserById(testUser.getId())).willReturn(testUser);
    String oldPassword = testUser.getPassword();
    userService.setRandomPassword(testUser.getId());
    assertNotEquals(oldPassword, testUser.getPassword());
  }
}
