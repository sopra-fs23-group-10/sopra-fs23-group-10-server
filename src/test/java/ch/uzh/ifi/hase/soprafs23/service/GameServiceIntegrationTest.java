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
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

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
    Game savedGame = gameRepository.save(prepGame);

    Game foundGame = gameService.searchGameById(savedGame.getGameId());

    assertEquals(savedGame.getGameId(), foundGame.getGameId());
    assertEquals(savedGame.getInvitingUserId(), foundGame.getInvitingUserId());
    assertEquals(savedGame.getInvitedUserId(), foundGame.getInvitedUserId());
    assertEquals(savedGame.getQuizType(), foundGame.getQuizType());
    assertEquals(savedGame.getModeType(), foundGame.getModeType());
    assertEquals(savedGame.getCurrentPlayer(), foundGame.getCurrentPlayer());
  }

  @Test
  void searchGameById_notFound_throwsException() {
    assertThrows(ResponseStatusException.class, () -> gameService.searchGameById(1L));
  }

  @Test
  void getGameIdOfUser_invitingUserId_success() {
    Game savedGame = gameRepository.save(prepGame);

    Long foundGameId = gameService.getGameIdOfUser(prepGame.getInvitingUserId());

    assertNotNull(foundGameId);
    assertEquals(savedGame.getGameId(), foundGameId);
  }

  @Test
  void getGameIdOfUser_invitedUserId_success() {
    Game savedGame = gameRepository.save(prepGame);

    Long foundGameId = gameService.getGameIdOfUser(prepGame.getInvitedUserId());

    assertNotNull(foundGameId);
    assertEquals(savedGame.getGameId(), foundGameId);
  }

  @Test
  void getGameIdOfUser_notFound_throwsException() {
    assertThrows(ResponseStatusException.class, () -> gameService.getGameIdOfUser(prepGame.getInvitedUserId()));
  }

  /*
  @Test
  void changeCurrentPlayer_success() {
    Game savedGame = gameRepository.save(prepGame);

    assertEquals(prepGame.getInvitedUserId(), savedGame.getCurrentPlayer());
    gameService.changeCurrentPlayer(savedGame.getGameId());
    assertEquals(prepGame.getInvitingUserId(), savedGame.getCurrentPlayer());
  }
  */
}
