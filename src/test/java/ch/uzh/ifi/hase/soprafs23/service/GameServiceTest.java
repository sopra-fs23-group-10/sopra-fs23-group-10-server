package ch.uzh.ifi.hase.soprafs23.service;


import ch.uzh.ifi.hase.soprafs23.constant.Category;
import ch.uzh.ifi.hase.soprafs23.constant.ModeType;
import ch.uzh.ifi.hase.soprafs23.constant.QuizType;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.handler.GameMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class GameServiceTest {

    @InjectMocks
    private GameService gameService;

    private Game prepTestTextDuelGame;

    private Game workingTestTextDuelGame;

    @BeforeEach
    public void setup() {
        prepTestTextDuelGame = new Game(0L, 1L, 2L, QuizType.TEXT, ModeType.DUEL);
        workingTestTextDuelGame = new Game(1L, 3L, 4L, QuizType.TEXT, ModeType.DUEL);

        MockitoAnnotations.openMocks(this);
        GameMap gameMap = mock(GameMap.class);
        setMock(gameMap);
        gameService.createGame(prepTestTextDuelGame.getInvitingUserId(), prepTestTextDuelGame.getInvitedUserId(), prepTestTextDuelGame.getQuizType(), prepTestTextDuelGame.getModeType());
    }

    private void setMock(GameMap mock) {
        try {
            Field instance = GameMap.class.getDeclaredField("instance");
            instance.setAccessible(true);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    public void resetGameMap() throws Exception {
        Field instance = GameMap.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    public void getGame_validInput_success() {
        Game foundGame = gameService.getGame(prepTestTextDuelGame.getId());

        assertEquals(prepTestTextDuelGame.getId(), foundGame.getId());
        assertEquals(prepTestTextDuelGame.getInvitedUserId(), foundGame.getInvitedUserId());
        assertEquals(prepTestTextDuelGame.getInvitingUserId(), foundGame.getInvitingUserId());
        assertEquals(prepTestTextDuelGame.getQuizType(), foundGame.getQuizType());
        assertEquals(prepTestTextDuelGame.getModeType(), foundGame.getModeType());
    }

    @Test
    public void createGame_validInput_success() {
        Game createdGame = gameService.createGame(workingTestTextDuelGame.getInvitingUserId(), workingTestTextDuelGame.getInvitedUserId(), workingTestTextDuelGame.getQuizType(), workingTestTextDuelGame.getModeType());

        assertNotNull(gameService.getGame(createdGame.getId()));

        assertEquals(workingTestTextDuelGame.getInvitedUserId(), createdGame.getInvitedUserId());
        assertEquals(workingTestTextDuelGame.getInvitingUserId(), createdGame.getInvitingUserId());
        assertEquals(workingTestTextDuelGame.getQuizType(), createdGame.getQuizType());
        assertEquals(workingTestTextDuelGame.getModeType(), createdGame.getModeType());
    }

    @Test
    public void removeGame_validInput_success() {
        gameService.removeGame(prepTestTextDuelGame.getId());
        assertNull(gameService.getGame(prepTestTextDuelGame.getId()));
    }

    @Test
    public void removeGame_nonValidInput_noChange() {
        assertNotNull(gameService.getGame(prepTestTextDuelGame.getId()));
        gameService.removeGame(workingTestTextDuelGame.getId());
        assertNotNull(gameService.getGame(prepTestTextDuelGame.getId()));
    }

    @Test
    public void getAllTopics_success() {
        Map<String, List<Category>> topicsMap = gameService.getAllTopics();
        List<Category> topicsList = topicsMap.get("topics");

        assertTrue(topicsList.contains(Category.ARTS_LITERATURE));
        assertTrue(topicsList.contains(Category.FILM_TV));
        assertTrue(topicsList.contains(Category.FOOD_DRINK));
        assertTrue(topicsList.contains(Category.GENERAL_KNOWLEDGE));
        assertTrue(topicsList.contains(Category.GEOGRAPHY));
        assertTrue(topicsList.contains(Category.MUSIC));
        assertTrue(topicsList.contains(Category.SCIENCE));
        assertTrue(topicsList.contains(Category.SOCIETY_CULTURE));
        assertTrue(topicsList.contains(Category.SPORT_LEISURE));
    }

    @Test
    public void getRandomTopics_success() {
        Map<String, List<Category>> topicsMap = gameService.getRandomTopics(prepTestTextDuelGame.getId(), prepTestTextDuelGame.getInvitedUserId());
        List<Category> topicsList = topicsMap.get("topics");

        assertEquals(3, topicsList.size());
    }

    @Test
    public void checkGame_gameExists() {
        gameService.checkGame(prepTestTextDuelGame.getId());
    }

    @Test
    public void checkGame_gameNotExists_exceptionRaised() {
        boolean tester = false;
        try {
            gameService.checkGame(workingTestTextDuelGame.getId());
        } catch (Exception e) {
            tester = true;
        }
        assertTrue(tester);
    }
}