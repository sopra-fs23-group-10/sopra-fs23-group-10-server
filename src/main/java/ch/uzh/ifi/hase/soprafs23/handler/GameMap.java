package ch.uzh.ifi.hase.soprafs23.handler;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import java.util.HashMap;


public class GameMap {
    private final HashMap<Long, Game> games = new HashMap<>();
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
