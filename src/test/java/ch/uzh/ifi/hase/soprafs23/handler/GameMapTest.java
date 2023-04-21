package ch.uzh.ifi.hase.soprafs23.handler;

import ch.uzh.ifi.hase.soprafs23.constant.ModeType;
import ch.uzh.ifi.hase.soprafs23.constant.QuizType;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameMapTest {

    @Test
    public void testSerialization() {
        Game game = new Game();
        game.setId(1L);
        game.setInvitingUserId(1L);
        game.setInvitedUserId(2L);
        game.setQuizType(QuizType.TEXT);
        game.setModeType(ModeType.DUEL);

        GameMap gameMap = GameMap.getInstance();
        gameMap.put(game);

        Game retrievedGame = gameMap.get(1L);

        assertEquals(game.getId(), retrievedGame.getId());
        assertEquals(game.getInvitingUserId(), retrievedGame.getInvitingUserId());
        assertEquals(game.getInvitedUserId(), retrievedGame.getInvitedUserId());
        assertEquals(game.getQuizType(), retrievedGame.getQuizType());
        assertEquals(game.getModeType(), retrievedGame.getModeType());
    }
}
