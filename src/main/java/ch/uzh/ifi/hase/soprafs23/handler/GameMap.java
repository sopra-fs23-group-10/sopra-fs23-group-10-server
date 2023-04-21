package ch.uzh.ifi.hase.soprafs23.handler;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class GameMap {
    private final List<Game> games = new ArrayList<>();
    private static GameMap instance;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private GameMap() {}

    public static GameMap getInstance() {
        if (instance == null) {
            synchronized (GameMap.class) {
                if (instance == null) {
                    instance = new GameMap();
                }
            }
        }
        return instance;
    }

    public Game get(long gameId) {
        lock.readLock().lock();
        try {
            for (Game game : games) {
                if (game.getId() == gameId) {
                    return game;
                }
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void put(Game game) {
        lock.writeLock().lock();
        try {
            games.add(game);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void remove(long gameId) {
        lock.writeLock().lock();
        try {
            Game gameToRemove = null;
            for (Game game : games) {
                if (game.getId() == gameId) {
                    gameToRemove = game;
                    break;
                }
            }
            if (gameToRemove != null) {
                games.remove(gameToRemove);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
}
