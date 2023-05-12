package ch.uzh.ifi.hase.soprafs23.service;


import ch.uzh.ifi.hase.soprafs23.constant.ModeType;
import ch.uzh.ifi.hase.soprafs23.constant.QuizType;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WebAppConfiguration
@SpringBootTest
public class GameServiceIntegrationTest {

  @Qualifier("gameRepository")
  @Autowired
  private GameRepository gameRepository;

  @Autowired
  private GameService gameService;

  @BeforeEach
  public void setup() {
    gameRepository.deleteAll();
  }

  @Test
  void createGame_success() {
    Game newGame = new Game();
    newGame.setInvitingUserId(11L);
    newGame.setInvitedUserId(12L);
    newGame.setQuizType(QuizType.TEXT);
    newGame.setModeType(ModeType.DUEL);
    newGame.setCurrentPlayer(newGame.getInvitedUserId());

    Game createdGame = gameService.createGame(newGame.getInvitingUserId(), newGame.getInvitedUserId(), newGame.getQuizType(), newGame.getModeType());

    assertEquals(newGame.getInvitingUserId(), createdGame.getInvitingUserId());
    assertEquals(newGame.getInvitedUserId(), createdGame.getInvitedUserId());
    assertEquals(newGame.getQuizType(), createdGame.getQuizType());
    assertEquals(newGame.getModeType(), createdGame.getModeType());
    assertEquals(newGame.getCurrentPlayer(), createdGame.getCurrentPlayer());
  }
}
