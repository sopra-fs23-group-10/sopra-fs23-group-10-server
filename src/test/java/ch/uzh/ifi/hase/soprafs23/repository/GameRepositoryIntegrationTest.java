package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.constant.ModeType;
import ch.uzh.ifi.hase.soprafs23.constant.QuizType;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
class GameRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GameRepository gameRepository;

    private Game game;

    @BeforeEach
    private void setup() {
        game = new Game();
        game.setInvitingUserId(45L);
        game.setInvitedUserId(67L);
        game.setQuizType(QuizType.TEXT);
        game.setModeType(ModeType.DUEL);
        game.setCurrentPlayer(game.getInvitedUserId());

        entityManager.persist(game);
        entityManager.flush();
    }

    @Test
    void findGameByGameId_success() {
        Game found = gameRepository.findGameByGameId(game.getGameId());

        assertNotNull(found);
        assertEquals(found.getGameId(), game.getGameId());
        assertEquals(found.getInvitingUserId(), game.getInvitingUserId());
        assertEquals(found.getInvitedUserId(), game.getInvitedUserId());
        assertEquals(found.getQuizType(), game.getQuizType());
        assertEquals(found.getModeType(), game.getModeType());
        assertEquals(found.getCurrentPlayer(), game.getCurrentPlayer());
    }

    @Test
    void findGameByGameId_noEntityFound() {
        Game found = gameRepository.findGameByGameId(-1L);

        assertNull(found);
    }

    @Test
    void deleteGameByGameId_success() {
        Game found = gameRepository.findGameByGameId(game.getGameId());

        assertNotNull(found);
        assertEquals(found.getGameId(), game.getGameId());
        assertEquals(found.getInvitingUserId(), game.getInvitingUserId());
        assertEquals(found.getInvitedUserId(), game.getInvitedUserId());
        assertEquals(found.getQuizType(), game.getQuizType());
        assertEquals(found.getModeType(), game.getModeType());
        assertEquals(found.getCurrentPlayer(), game.getCurrentPlayer());

        gameRepository.deleteGameByGameId(game.getGameId());

        found = gameRepository.findGameByGameId(game.getGameId());

        assertNull(found);
    }

    @Test
    void deleteGameByGameId_noGameWithId() {
        Game found = gameRepository.findGameByGameId(game.getGameId());

        assertNotNull(found);
        assertEquals(found.getGameId(), game.getGameId());
        assertEquals(found.getInvitingUserId(), game.getInvitingUserId());
        assertEquals(found.getInvitedUserId(), game.getInvitedUserId());
        assertEquals(found.getQuizType(), game.getQuizType());
        assertEquals(found.getModeType(), game.getModeType());
        assertEquals(found.getCurrentPlayer(), game.getCurrentPlayer());

        gameRepository.deleteGameByGameId(-1);

        found = gameRepository.findGameByGameId(game.getGameId());

        assertNotNull(found);
        assertEquals(found.getGameId(), game.getGameId());
        assertEquals(found.getInvitingUserId(), game.getInvitingUserId());
        assertEquals(found.getInvitedUserId(), game.getInvitedUserId());
        assertEquals(found.getQuizType(), game.getQuizType());
        assertEquals(found.getModeType(), game.getModeType());
        assertEquals(found.getCurrentPlayer(), game.getCurrentPlayer());
    }

    @Test
    void findGameByInvitingUserId_success() {
        Game found = gameRepository.findGameByInvitingUserId(game.getInvitingUserId());

        assertNotNull(found);
        assertEquals(found.getGameId(), game.getGameId());
        assertEquals(found.getInvitingUserId(), game.getInvitingUserId());
        assertEquals(found.getInvitedUserId(), game.getInvitedUserId());
        assertEquals(found.getQuizType(), game.getQuizType());
        assertEquals(found.getModeType(), game.getModeType());
        assertEquals(found.getCurrentPlayer(), game.getCurrentPlayer());
    }

    @Test
    void findGameByInvitingUserId_noneFound() {
        Game found = gameRepository.findGameByInvitingUserId(4L);

        assertNull(found);
    }

    @Test
    void findGameByInvitedUserId_success() {
        Game found = gameRepository.findGameByInvitedUserId(game.getInvitedUserId());

        assertNotNull(found);
        assertEquals(found.getGameId(), game.getGameId());
        assertEquals(found.getInvitingUserId(), game.getInvitingUserId());
        assertEquals(found.getInvitedUserId(), game.getInvitedUserId());
        assertEquals(found.getQuizType(), game.getQuizType());
        assertEquals(found.getModeType(), game.getModeType());
        assertEquals(found.getCurrentPlayer(), game.getCurrentPlayer());
    }

    @Test
    void findGameByInvitedUserId_noneFound() {
        Game found = gameRepository.findGameByInvitedUserId(6L);

        assertNull(found);
    }
}