package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.ModeType;
import ch.uzh.ifi.hase.soprafs23.constant.QuizType;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


@Service
@Transactional
public class GameService {
    private final GameRepository gameRepository;

    @Autowired
    public GameService(@Qualifier("gameRepository") GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public Game createGame(Long invitingUserId, Long invitedUserId, QuizType quizType, ModeType modeType) {
        Game game = new Game();
        game.setInvitingUserId(invitingUserId);
        game.setInvitedUserId(invitedUserId);
        game.setQuizType(quizType);
        game.setModeType(modeType);
        game.setCurrentPlayer(invitedUserId);
        game = gameRepository.save(game);
        return game;
    }

    public Game searchGameById(Long gameId) {
        Game game = gameRepository.findGameByGameId(gameId);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game with specified ID cannot be found.");
        }
        return game;
    }

    public long getGameIdOfUser(Long userId) {
        Game invitingGame = gameRepository.findGameByInvitingUserId(userId);
        if (invitingGame != null) {
            return invitingGame.getGameId();
        }
        Game invitedGame = gameRepository.findGameByInvitedUserId(userId);
        if (invitedGame != null) {
            return invitedGame.getGameId();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Specified user is not in any game.");
    }

    public synchronized void changeCurrentPlayer(long gameId) {
        Game game = gameRepository.findGameByGameId(gameId);
        game.setCurrentPlayer((game.getCurrentPlayer() == game.getInvitingUserId()) ? game.getInvitedUserId() : game.getInvitingUserId());
    }
}
