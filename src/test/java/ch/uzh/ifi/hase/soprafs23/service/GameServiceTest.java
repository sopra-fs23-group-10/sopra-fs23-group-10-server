package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.ModeType;
import ch.uzh.ifi.hase.soprafs23.constant.QuizType;
import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameService gameService;

    private Game prepTextDuelGame;

    private User invitingUser;
    private User invitedUser;

    @BeforeEach
    private void setup() {invitingUser = new User();
        MockitoAnnotations.openMocks(this);

        invitingUser.setId(1L);
        invitingUser.setStatus(UserStatus.ONLINE);

        invitedUser = new User();
        invitedUser.setId(2L);
        invitedUser.setStatus(UserStatus.ONLINE);

        prepTextDuelGame = new Game();
        prepTextDuelGame.setGameId(0L);
        prepTextDuelGame.setInvitingUserId(invitingUser.getId());
        prepTextDuelGame.setInvitedUserId(invitedUser.getId());
        prepTextDuelGame.setQuizType(QuizType.TEXT);
        prepTextDuelGame.setModeType(ModeType.DUEL);
        prepTextDuelGame.setCurrentPlayer(invitedUser.getId());
    }

    @Test
    void createGame_success() {
        given(gameRepository.save(Mockito.any())).willReturn(prepTextDuelGame);

        Game game = gameService.createGame(prepTextDuelGame.getInvitingUserId(), prepTextDuelGame.getInvitedUserId(), prepTextDuelGame.getQuizType(), prepTextDuelGame.getModeType());

        assertNotNull(game);
        assertEquals(prepTextDuelGame.getGameId(), game.getGameId());
        assertEquals(prepTextDuelGame.getInvitingUserId(), game.getInvitingUserId());
        assertEquals(prepTextDuelGame.getInvitedUserId(), game.getInvitedUserId());
        assertEquals(prepTextDuelGame.getQuizType(), game.getQuizType());
        assertEquals(prepTextDuelGame.getModeType(), game.getModeType());
        assertEquals(prepTextDuelGame.getCurrentPlayer(), game.getCurrentPlayer());
    }

    @Test
    void searchGameById_success() {
        given(gameRepository.findGameByGameId(prepTextDuelGame.getGameId())).willReturn(prepTextDuelGame);

        Game game = gameService.searchGameById(prepTextDuelGame.getGameId());

        assertNotNull(game);
        assertEquals(prepTextDuelGame.getGameId(), game.getGameId());
        assertEquals(prepTextDuelGame.getInvitingUserId(), game.getInvitingUserId());
        assertEquals(prepTextDuelGame.getInvitedUserId(), game.getInvitedUserId());
        assertEquals(prepTextDuelGame.getQuizType(), game.getQuizType());
        assertEquals(prepTextDuelGame.getModeType(), game.getModeType());
        assertEquals(prepTextDuelGame.getCurrentPlayer(), game.getCurrentPlayer());
    }

    @Test
    void getGameIdOfUser_success() {
        given(gameRepository.findGameByInvitingUserId(prepTextDuelGame.getInvitingUserId())).willReturn(prepTextDuelGame);

        long gameIdOfUser = gameService.getGameIdOfUser(prepTextDuelGame.getInvitingUserId());

        assertEquals(prepTextDuelGame.getGameId(), gameIdOfUser);
    }

    @Test
    void testGetGameIdOfUser_noGameFound_throwsNotFound_404() {
        // instantiating idlingUser as a new instance of class User
        // and setting the ID to 56L
        User idlingUser = new User();
        idlingUser.setId(56L);

        // Both method calls to gameRepository are mocked to return null,
        // implying that idlingUser is not currently in a Game
        given(gameRepository.findGameByInvitingUserId(idlingUser.getId()))
                .willReturn(null);
        given(gameRepository.findGameByInvitedUserId(idlingUser.getId()))
                .willReturn(null);

        // The outcome of the method call is asserted,
        // expecting the method to throw a ResponseStatusException
        Exception exception = assertThrows(ResponseStatusException.class,
                () -> gameService.getGameIdOfUser(idlingUser.getId()));

        // Assert if the right message is thrown with the exception
        String expectedMessage = "Specified user is not in any game.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        // Assert if the right status code is thrown with the exception
        String expectedStatusCode = "404 NOT FOUND";
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void changeCurrentPlayer_success() {
        given(gameRepository.findGameByGameId(prepTextDuelGame.getGameId())).willReturn(prepTextDuelGame);

        assertEquals(invitedUser.getId(), prepTextDuelGame.getCurrentPlayer());
        gameService.changeCurrentPlayer(prepTextDuelGame.getGameId());
        assertEquals(invitingUser.getId(), prepTextDuelGame.getCurrentPlayer());
    }
}