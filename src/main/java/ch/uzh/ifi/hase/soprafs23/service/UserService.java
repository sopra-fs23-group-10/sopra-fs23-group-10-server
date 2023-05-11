package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.UserResultTuple;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.util.*;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;

  private final MailSenderService mailSenderService = new MailSenderService();

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * This method verifies a provided token by getting the
   * matching user from the repository.
   * The matching user is returned or exception is thrown.
   *
   * @param token
   * @throws org.springframework.web.server.ResponseStatusException
   * @return User userByToken
   */
  public User verifyToken(String token) {
    User userByToken = userRepository.findUserByToken(token);

    if (userByToken == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Provided token is invalid.");
    }

    return userByToken;
  }

  /**
   * This method validates a user by comparing a provided token and id with
   * the corresponding user in the user repository.
   *
   * @param token
   * @param userId
   * @throws org.springframework.web.server.ResponseStatusException
   */
  public User verifyTokenWithId(String token, long userId) {
      User userByToken = verifyToken(token);

      User userById = searchUserById(userId);

      if (userByToken.getId() != userById.getId() || !userByToken.getToken().equals(userById.getToken())) {
          throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized.");
      }
      return userByToken;
  }

  /**
   * This method returns all users currently in the userRepository.
   *
   * @return List<User>
   */
  public List<User> getUsers() {
    List<User> users = this.userRepository.findAll();
    users.removeIf(user -> Objects.equals(user.getId(), 0L));
    return users;
  }

  public List<User> getOnlineUsers() {
    List<User> allUsers = this.getUsers();
    allUsers.removeIf(user -> !Objects.equals(user.getStatus(), UserStatus.ONLINE));
    return allUsers;
  }

  /**
   * This method is required to create a new user with the provided data in the repository
   *
   * @param newUser
   * @return User newUser
   */
  public User createUser(User newUser) {
    newUser.setToken(UUID.randomUUID().toString());
    newUser.setStatus(UserStatus.ONLINE);
    newUser.setProfilePicture(newUser.getUsername());
    newUser.setPoints(0L);
    newUser.setRank(this.calculateRanks());
    checkIfUserExists(newUser);
    newUser = userRepository.save(newUser);
    userRepository.flush();

    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }

/**
 * This is a helper method that will check the uniqueness criteria of the
 * username and the name
 * defined in the User entity. The method will do nothing if the input is unique
 * and throw an error otherwise.
 *
 * @param userToBeCreated
 * @throws org.springframework.web.server.ResponseStatusException
 * @see User
 */
  private void checkIfUserExists(User userToBeCreated) {
    User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());
    User userByEmail = userRepository.findByEmail(userToBeCreated.getEmail());

    String baseErrorMessage = "The %s provided %s already taken. Therefore, the user could not be created.";
    if (userByUsername != null && userByEmail != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "username and email", "are"));
    }
    if (userByUsername != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "username", "is"));
    }
    if (userByEmail != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "email", "is"));
    }

  }

  /**
   * This method searches a user by ID, which is returned when found. Else an exception is thrown.
   *
   * @param userId
   * @throws org.springframework.web.server.ResponseStatusException
   * @return User userById
   */
  public User searchUserById(Long userId) {
    User userById = userRepository.findUserById(userId);

    if (userById == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with specified ID does not exist.");
    }

    return userById;
  }

  /**
   * This method searches for an existing username in the repository and validates the provided password.
   *
   * @param username
   * @param password
   * @throws org.springframework.web.server.ResponseStatusException
   * @return User userByUsername
   */
  public User checkLoginCredentials(String username, String password) {
    User userByUsername = userRepository.findByUsername(username);

    if (userByUsername == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Username does not exist.");
    } else if (!userByUsername.getPassword().equals(password)) {
      throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Wrong password.");
    }
    if (userByUsername.getStatus() != UserStatus.OFFLINE) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is already logged in.");
    }

    return userByUsername;
  }

  /**
   * This method searches a user via the provided ID and changes its username and birthdayDate if the user exists.
   *
   * @param userWithAdjustments
   * @param userId
   * @throws org.springframework.web.server.ResponseStatusException
   * @return User userById
   */
  public User changeUsernameAndProfilePic(long userId, User userWithAdjustments) {
    User userById = this.userRepository.findUserById(userId);

    if (userById == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with specified userID does not exist.");
    }

    if (!userById.getUsername().equals(userWithAdjustments.getUsername())) {
      checkIfUserExists(userWithAdjustments);
      userById.setUsername(userWithAdjustments.getUsername());
    }
    userById.setProfilePicture(userWithAdjustments.getProfilePicture());

    return userById;
  }

  /**
   * This method sets a user's status to ONLINE
   *
   * @param userId
   */
  public void setOnline(Long userId) {
    User user = userRepository.findUserById(userId);
    user.setStatus(UserStatus.ONLINE);
    userRepository.save(user);
    userRepository.flush();
  }

  /**
   * This method sets a user's status to offline.
   *
   * @param userId
   * @throws org.springframework.web.server.ResponseStatusException
   */
  public void setOffline(Long userId) {
    User user = searchUserById(userId);
    user.setStatus(UserStatus.OFFLINE);
    userRepository.save(user);
    userRepository.flush();
  }

  public void setInGame(long userId) {
    User user = userRepository.findUserById(userId);
    if (user == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User cannot be found.");
    }
    user.setStatus(UserStatus.IN_GAME);
  }
  /**
   * This method updates a user's points at the end of a game.
   *
   * @param userResultTuple
   * @throws org.springframework.web.server.ResponseStatusException
   */
  public void updatePoints(UserResultTuple userResultTuple) {
    User invitingUser = searchUserById(userResultTuple.getInvitingPlayerId());
    User invitedUser = searchUserById(userResultTuple.getInvitedPlayerId());

    invitingUser.setPoints(invitingUser.getPoints() + userResultTuple.getInvitingPlayerResult());
    invitedUser.setPoints(invitedUser.getPoints() + userResultTuple.getInvitedPlayerResult());
    userRepository.save(invitedUser);
    userRepository.save(invitingUser);
    this.calculateRanks();
  }

  public long calculateRanks(){
    List<User> users = this.getUsers();
    users.sort(Comparator.comparingLong(User::getPoints).reversed());
    long rank = 1;
    for (User user : users) {
      user.setRank(rank++);
      userRepository.save(user);
    }
    userRepository.flush();
    return rank;
  }

  public void sendNewPassword(User user) {
    User emailUser = userRepository.findByEmail(user.getEmail());

    if (emailUser == null) {
      log.error("Email in password reset request does not exist.");
    } else {
      this.setRandomPassword(emailUser.getId());
      mailSenderService.sendNewPassword(emailUser);
    }
  }

  public void setRandomPassword(long userId) {
    final SecureRandom secureRandom = new SecureRandom();
    char[] allCharacters = "abcdefghijklmnopqrstuvwxyz0123456789/*-+".toCharArray();

    StringBuilder stringBuilder = new StringBuilder();

    for (int i = 0; i < 12; i++) {
      stringBuilder.append(allCharacters[secureRandom.nextInt(allCharacters.length)]);
    }

    String generatedPassword = stringBuilder.toString();

    User user = userRepository.findUserById(userId);
    user.setPassword(generatedPassword);
  }
}
