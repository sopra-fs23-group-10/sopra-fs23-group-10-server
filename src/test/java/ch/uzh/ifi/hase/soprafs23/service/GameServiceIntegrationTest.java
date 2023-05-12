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

  private Game prepGame;

  @BeforeEach
  public void setup() {
    gameRepository.deleteAll();

    prepGame = new Game();
    prepGame.setInvitingUserId(11L);
    prepGame.setInvitedUserId(12L);
    prepGame.setQuizType(QuizType.TEXT);
    prepGame.setModeType(ModeType.DUEL);
    prepGame.setCurrentPlayer(prepGame.getInvitedUserId());
  }

  @Test
  void createGame_success() {
    Game createdGame = gameService.createGame(prepGame.getInvitingUserId(), prepGame.getInvitedUserId(), prepGame.getQuizType(), prepGame.getModeType());

    assertEquals(prepGame.getInvitingUserId(), createdGame.getInvitingUserId());
    assertEquals(prepGame.getInvitedUserId(), createdGame.getInvitedUserId());
    assertEquals(prepGame.getQuizType(), createdGame.getQuizType());
    assertEquals(prepGame.getModeType(), createdGame.getModeType());
    assertEquals(prepGame.getCurrentPlayer(), createdGame.getCurrentPlayer());
  }

  @Test
  void searchGameById_success() {
    gameRepository.save(prepGame);

    Game foundGame = gameService.searchGameById(1L);

    assertEquals(prepGame.getInvitingUserId(), foundGame.getInvitingUserId());
    assertEquals(prepGame.getInvitedUserId(), foundGame.getInvitedUserId());
    assertEquals(prepGame.getQuizType(), foundGame.getQuizType());
    assertEquals(prepGame.getModeType(), foundGame.getModeType());
    assertEquals(prepGame.getCurrentPlayer(), foundGame.getCurrentPlayer());
  }
}
