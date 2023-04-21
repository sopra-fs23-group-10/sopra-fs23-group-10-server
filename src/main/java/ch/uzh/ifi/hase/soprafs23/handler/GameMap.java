package ch.uzh.ifi.hase.soprafs23.handler;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class GameMap {
    private final Map<Long, Game> games = Collections.synchronizedMap(new HashMap<Long, Game>());
    private static GameMap instance;

    private GameMap() {}

    public static synchronized GameMap getInstance() {
        if (instance == null) {
            instance = new GameMap();
        }
        return instance;
    }

    public Game get(Long gameId) {
        return this.games.get(gameId);
    }

    public void put(Game game) {
        this.games.put(game.getId(), game);
    }

    public void remove(Long gameId) {
        this.games.remove(gameId);
    }
}
