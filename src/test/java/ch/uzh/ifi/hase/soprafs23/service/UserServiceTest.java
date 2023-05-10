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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

  private User testUser;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);

    // given
    testUser = new User();
    testUser.setId(1L);
    testUser.setUsername("testUsername");
    testUser.setPassword("testPassword");
    testUser.setPoints(0L);

    // when -> any object is being saved in the userRepository -> return the dummy
    // testUser
    Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
  }

  @Test
  void createUser_validInputs_success() {
    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    User createdUser = userService.createUser(testUser);

    // then
    Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

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
    void changeUsername_validInputs_success() {
        // when -> any object is being save in the userRepository -> return the dummy
        // createdUser
        User createdUser = userService.createUser(testUser);
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());
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

      Mockito.verify(userRepository, Mockito.times(3)).save(Mockito.any());
      Mockito.verify(userRepository, Mockito.times(1)).flush();

      assertEquals(4L, nextLowestRank);
      assertEquals(1L, user1.getRank());
      assertEquals(2L, user2.getRank());
      assertEquals(3L, user3.getRank());
    }

  @Test
  void setRandomlyGeneratedPassword_success() {
    given(userRepository.findUserById(testUser.getId())).willReturn(testUser);
    String oldPassword = testUser.getPassword();
    userService.setRandomPassword(testUser.getId());
    assertNotEquals(oldPassword, testUser.getPassword());
  }
}
