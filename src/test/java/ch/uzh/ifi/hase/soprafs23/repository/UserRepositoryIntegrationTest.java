package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private UserRepository userRepository;

  @Test
  public void findByUsername_success() {
    // given
    User user = new User();
    user.setUsername("firstname@lastname");
    user.setPassword("testPassword");
    user.setEmail("email@email.com");
    user.setPoints(2L);
    user.setProfilePicture("firstname@lastname");
    user.setStatus(UserStatus.OFFLINE);
    user.setToken("1");

    entityManager.persist(user);
    entityManager.flush();

    // when
    User found = userRepository.findByUsername(user.getUsername());

    // then
    assertNotNull(found.getId());
    assertEquals(found.getUsername(), user.getUsername());
    assertEquals(found.getPassword(), user.getPassword());
    assertEquals(found.getEmail(), user.getEmail());
    assertEquals(found.getPoints(), user.getPoints());
    assertEquals(found.getProfilePicture(), user.getProfilePicture());
    assertEquals(found.getId(), user.getId());
    assertEquals(found.getToken(), user.getToken());
    assertEquals(found.getStatus(), user.getStatus());
  }

    @Test
    public void findByUsername_failure() {
        // given
        User user = new User();
        user.setUsername("firstname@lastname");
        user.setPassword("testPassword");
        user.setEmail("email@email.com");
        user.setPoints(2L);
        user.setProfilePicture("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);
        user.setToken("1");

        entityManager.persist(user);
        entityManager.flush();

        // when
        User found = userRepository.findByUsername("someUsername");

        // then
        assertNull(found);
    }

    @Test
    public void findUserById_success() {
        // given
        User user = new User();
        user.setUsername("firstname@lastname");
        user.setPassword("testPassword");
        user.setEmail("email@email.com");
        user.setPoints(2L);
        user.setProfilePicture("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);
        user.setToken("1");

        entityManager.persist(user);
        entityManager.flush();

        // when
        User found = userRepository.findUserById(user.getId());

        // then
        assertNotNull(found.getId());
        assertEquals(found.getUsername(), user.getUsername());
        assertEquals(found.getPassword(), user.getPassword());
        assertEquals(found.getEmail(), user.getEmail());
        assertEquals(found.getPoints(), user.getPoints());
        assertEquals(found.getProfilePicture(), user.getProfilePicture());
        assertEquals(found.getId(), user.getId());
        assertEquals(found.getToken(), user.getToken());
        assertEquals(found.getStatus(), user.getStatus());
    }

    @Test
    public void findUserById_failure() {
        // given
        User user = new User();
        user.setUsername("firstname@lastname");
        user.setPassword("testPassword");
        user.setEmail("email@email.com");
        user.setPoints(2L);
        user.setProfilePicture("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);
        user.setToken("1");

        entityManager.persist(user);
        entityManager.flush();

        // when
        User found = userRepository.findUserById(-1L);

        // then
        assertNull(found);
    }

    @Test
    public void findUserByToken_success() {
        // given
        User user = new User();
        user.setUsername("firstname@lastname");
        user.setPassword("testPassword");
        user.setEmail("email@email.com");
        user.setPoints(2L);
        user.setProfilePicture("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);
        user.setToken("1");

        entityManager.persist(user);
        entityManager.flush();

        // when
        User found = userRepository.findUserByToken(user.getToken());

        // then
        assertNotNull(found.getId());
        assertEquals(found.getUsername(), user.getUsername());
        assertEquals(found.getPassword(), user.getPassword());
        assertEquals(found.getEmail(), user.getEmail());
        assertEquals(found.getPoints(), user.getPoints());
        assertEquals(found.getProfilePicture(), user.getProfilePicture());
        assertEquals(found.getId(), user.getId());
        assertEquals(found.getToken(), user.getToken());
        assertEquals(found.getStatus(), user.getStatus());
    }

    @Test
    public void findUserByToken_failure() {
        // given
        User user = new User();
        user.setUsername("firstname@lastname");
        user.setPassword("testPassword");
        user.setEmail("email@email.com");
        user.setPoints(2L);
        user.setProfilePicture("firstname@lastname");
        user.setEmail("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);
        user.setToken("1");

        entityManager.persist(user);
        entityManager.flush();

        // when
        User found = userRepository.findUserByToken("invalidToken");

        // then
        assertNull(found);
    }

    @Test
    public void updatePoints_success() {
        // given
        User user = new User();
        user.setUsername("firstname@lastname");
        user.setPassword("testPassword");
        user.setEmail("email@email.com");
        user.setPoints(0L);
        user.setProfilePicture("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);
        user.setToken("1");

        entityManager.persist(user);
        entityManager.flush();

        user.setPoints(4L);

        // when
        userRepository.updatePoints(4L,user.getId());
        User found = userRepository.findUserById(user.getId());

        // then
        assertEquals(found.getId(), user.getId());
        assertEquals(found.getUsername(), user.getUsername());
        assertEquals(found.getPassword(), user.getPassword());
        assertEquals(found.getEmail(), user.getEmail());
        assertEquals(found.getPoints(), 4L);
        assertEquals(found.getProfilePicture(), user.getProfilePicture());
        assertEquals(found.getId(), user.getId());
        assertEquals(found.getToken(), user.getToken());
        assertEquals(found.getStatus(), user.getStatus());
    }
}
