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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

        if (userByToken.getStatus() != UserStatus.ONLINE) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User with provided token is currently in game or not logged in.");
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

        if (!userByToken.getId().equals(userById.getId()) || !userByToken.getToken().equals(userById.getToken())) {
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
        return this.userRepository.findAll();
    }

    public List<User> getOnlineUsers() {
        List<User> allUsers = this.userRepository.findAll();
        List<User> allOnlineUsers = new ArrayList<>();
        for (User user : allUsers) {
            if (user.getStatus() != UserStatus.OFFLINE) {
                allOnlineUsers.add(user);
            }
        }
        return allOnlineUsers;
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
        checkIfUserExists(newUser);
        // saves the given entity but data is only persisted in the database once
        // flush() is called
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
    public User changeUsername(long userId, User userWithAdjustments) {
        User userById = this.userRepository.findUserById(userId);

        if (userById == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with specified userID does not exist.");
        }

        if (!userById.getUsername().equals(userWithAdjustments.getUsername())) {
            checkIfUserExists(userWithAdjustments);
            userById.setUsername(userWithAdjustments.getUsername());
        }

        return userById;
    }

    /**
     * This method sets a user's status to ONLINE
     *
     * @param checkedUser
     */
    public void setOnline(User checkedUser) {
        checkedUser.setStatus(UserStatus.ONLINE);
    }

    /**
     * This method sets a user's status to offline.
     *
     * @param user
     * @param userId
     * @throws org.springframework.web.server.ResponseStatusException
     */
    public void setOffline(User user, Long userId) {
        if (user == null || userId == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User to be logged out was not found.");
        }
        if (!user.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized.");
        }
        user.setStatus(UserStatus.OFFLINE);
    }
    /**
     * This method updates a user's points at the end of a game.
     *
     * @param userResultTuple
     * @throws org.springframework.web.server.ResponseStatusException
     */
    public void updatePoints(UserResultTuple userResultTuple) {
        User invitedUser = searchUserById(userResultTuple.getInvitedPlayerId());
        User invitingUser = searchUserById(userResultTuple.getInvitingPlayerId());

        long invitedUserPoints = invitedUser.getPoints()+userResultTuple.getInvitedPlayerResult();
        long invitingUserPoints = invitingUser.getPoints()+userResultTuple.getInvitingPlayerResult();

        invitingUser.setPoints(invitingUser.getPoints() + invitingUserPoints);
        invitedUser.setPoints(invitedUser.getPoints() + invitedUserPoints);

        /*
        userRepository.updatePoints(invitedUserPoints, invitedUser.getId());
        userRepository.updatePoints(invitingUserPoints, invitingUser.getId());
         */
    }

    public long getPoints(long userId) {
        return userRepository.findUserById(userId).getPoints();
    }
}
