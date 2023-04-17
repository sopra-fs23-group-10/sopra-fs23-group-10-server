package ch.uzh.ifi.hase.soprafs23.service;


import ch.uzh.ifi.hase.soprafs23.constant.Category;
import ch.uzh.ifi.hase.soprafs23.constant.ModeType;
import ch.uzh.ifi.hase.soprafs23.constant.QuizType;
import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.controller.WebSocketController;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Question;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.UserAnswerTuple;
import ch.uzh.ifi.hase.soprafs23.handler.GameMap;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserResultTupleDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GameServiceTest {

    @InjectMocks
    private GameService gameService;

    @Mock
    private UserService userService;

    @Mock
    private WebSocketController webSocketController;

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
    public void getRandomTopics_once_success() {
        Map<String, List<Category>> topicsMap = gameService.getRandomTopics(prepTestTextDuelGame.getId(), prepTestTextDuelGame.getInvitedUserId());
        List<Category> topicsList = topicsMap.get("topics");

        assertEquals(3, topicsList.size());
    }

    @Test
    public void getRandomTopics_rotating_success() {
        int counterInvited = 0;
        int counterInviting = 0;

        for (int i = 0; i < 5; i++) {
            Map<String, List<Category>> topicsMap = gameService.getRandomTopics(prepTestTextDuelGame.getId(), prepTestTextDuelGame.getInvitedUserId());
            List<Category> topicsList = topicsMap.get("topics");
            assertEquals(3, topicsList.size());
            counterInvited += 1;

            topicsMap = gameService.getRandomTopics(prepTestTextDuelGame.getId(), prepTestTextDuelGame.getInvitingUserId());
            topicsList = topicsMap.get("topics");
            assertEquals(3, topicsList.size());
            counterInviting += 1;
        }
        assertEquals(counterInvited, counterInviting);
    }

    @Test
    public void getRandomTopics_requestedTwice_throwsException() {
        boolean tester = false;

        Map<String, List<Category>> topicsMap = gameService.getRandomTopics(prepTestTextDuelGame.getId(), prepTestTextDuelGame.getInvitedUserId());
        List<Category> topicsList = topicsMap.get("topics");
        assertEquals(3, topicsList.size());

        try {
            gameService.getRandomTopics(prepTestTextDuelGame.getId(), prepTestTextDuelGame.getInvitedUserId());
        } catch (Exception e) {
            tester = true;
        }

        assertTrue(tester);
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

    @Test
    public void getQuestion_validInput_success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUsername");
        user.setPassword("testPassword");
        user.setPoints(2L);
        user.setEmail("email@email.com");
        user.setProfilePicture("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);

        when(userService.searchUserById(Mockito.any())).thenReturn(user);

        Question receivedQuestion = gameService.getQuestion(Category.MUSIC, prepTestTextDuelGame.getId());
        assertNotNull(receivedQuestion);
        assertEquals(receivedQuestion.getClass(), Question.class);
    }

    @Test
    public void getQuestion_checkTopics_success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUsername");
        user.setPassword("testPassword");
        user.setPoints(2L);
        user.setEmail("email@email.com");
        user.setProfilePicture("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.IN_GAME);

        when(userService.searchUserById(Mockito.any())).thenReturn(user);

        List<Category> allTopics = new ArrayList<>(Arrays.asList(Category.values()));

        for (Category category : allTopics) {
            Question receivedQuestion = gameService.getQuestion(category, prepTestTextDuelGame.getId());
            assertNotNull(receivedQuestion);
            assertEquals(category, receivedQuestion.getCategory());
        }
    }

    @Test
    public void getQuestion_invalidID_throwsException() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUsername");
        user.setPassword("testPassword");
        user.setPoints(2L);
        user.setEmail("email@email.com");
        user.setProfilePicture("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);

        when(userService.searchUserById(Mockito.any())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User with specified ID does not exist."));

        boolean tester = false;
        try {
            gameService.getQuestion(Category.MUSIC, prepTestTextDuelGame.getId());
        } catch (Exception e) {
            tester = true;
        }
        assertTrue(tester);
    }
/*
    @Test
    public void answerQuestion_success() {
        User invitingUser = new User();
        invitingUser.setId(1L);
        invitingUser.setUsername("oneUser");
        invitingUser.setPassword("testPassword");
        invitingUser.setPoints(2L);
        invitingUser.setEmail("oneUser@email.com");
        invitingUser.setProfilePicture("oneUser");
        invitingUser.setToken("1");
        invitingUser.setStatus(UserStatus.IN_GAME);

        User invitedUser = new User();
        invitedUser.setId(2L);
        invitedUser.setUsername("anotherUser");
        invitedUser.setPassword("testPassword");
        invitedUser.setPoints(2L);
        invitedUser.setEmail("anotherUser@email.com");
        invitedUser.setProfilePicture("anotherUser");
        invitedUser.setToken("2");
        invitedUser.setStatus(UserStatus.IN_GAME);

        String[] incorrectAnswers = {"Neil Young", "Eric Clapton", "Elton John"};
        Question createdQuestion = new Question("622a1c357cc59eab6f94ff56",
                Category.MUSIC,
                "Bob Dylan",
                "Which musician has famously performed over 3,000 shows in their 'Never Ending Tour'?",
                incorrectAnswers);

        Game game = gameService.getGame(prepTestTextDuelGame.getId());
        game.addQuestion(createdQuestion);

        given(userService.searchUserById(invitingUser.getId())).willReturn(invitingUser);
        given(userService.searchUserById(invitedUser.getId())).willReturn(invitedUser);

        UserAnswerTuple invitingUserAnswerTuple = new UserAnswerTuple(invitingUser.getId(), createdQuestion.getId(), createdQuestion.getIncorrectAnswers()[1], 200L);
        UserAnswerTuple invitedUserAnswerTuple = new UserAnswerTuple(invitedUser.getId(), createdQuestion.getId(), createdQuestion.getCorrectAnswer(), 300L);

        UserResultTupleDTO invitingUserResultTupleDTO = gameService.answerQuestion(prepTestTextDuelGame.getId(), invitingUserAnswerTuple, webSocketController);
        UserResultTupleDTO invitedUserResultTupleDTO = gameService.answerQuestion(prepTestTextDuelGame.getId(), invitedUserAnswerTuple, webSocketController);


        assertEquals(prepTestTextDuelGame.getId(), invitingUserResultTupleDTO.getGameId());
        assertEquals(prepTestTextDuelGame.getInvitingUserId(), invitingUserResultTupleDTO.getInvitingPlayerId());
        assertEquals(0L, invitingUserResultTupleDTO.getInvitingPlayerResult());
        assertEquals(prepTestTextDuelGame.getInvitedUserId(), invitingUserResultTupleDTO.getInvitedPlayerId());
        assertEquals((500L * (0.5 * invitedUserAnswerTuple.getAnsweredTime())), invitedUserResultTupleDTO.getInvitedPlayerResult());

        assertEquals(prepTestTextDuelGame.getId(), invitedUserResultTupleDTO.getGameId());
        assertEquals(prepTestTextDuelGame.getInvitedUserId(), invitedUserResultTupleDTO.getInvitedPlayerId());
        assertEquals(prepTestTextDuelGame.getInvitingUserId(), invitedUserResultTupleDTO.getInvitingPlayerId());
    }*/

    @Test
    public void answerQuestion_nullAnswer_throwsException() {
        boolean tester = false;
        try {
            gameService.answerQuestion(prepTestTextDuelGame.getId(), null, webSocketController);
        } catch (Exception e) {
            tester = true;
        }
        assertTrue(tester);
    }
}