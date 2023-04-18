package ch.uzh.ifi.hase.soprafs23.service;


import ch.uzh.ifi.hase.soprafs23.constant.Category;
import ch.uzh.ifi.hase.soprafs23.constant.ModeType;
import ch.uzh.ifi.hase.soprafs23.constant.QuizType;
import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.controller.WebSocketController;
import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.handler.GameMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.util.*;

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

    private Game prepTextDuelGame;

    private Game workingTextDuelGame;

    @BeforeEach
    public void setup() {
        prepTextDuelGame = new Game(0L, 1L, 2L, QuizType.TEXT, ModeType.DUEL);
        workingTextDuelGame = new Game(1L, 3L, 4L, QuizType.TEXT, ModeType.DUEL);

        MockitoAnnotations.openMocks(this);
        GameMap gameMap = mock(GameMap.class);
        setMock(gameMap);
        gameService.createGame(prepTextDuelGame.getInvitingUserId(), prepTextDuelGame.getInvitedUserId(), prepTextDuelGame.getQuizType(), prepTextDuelGame.getModeType());
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
        Game foundGame = gameService.getGame(prepTextDuelGame.getId());

        assertEquals(prepTextDuelGame.getId(), foundGame.getId());
        assertEquals(prepTextDuelGame.getInvitedUserId(), foundGame.getInvitedUserId());
        assertEquals(prepTextDuelGame.getInvitingUserId(), foundGame.getInvitingUserId());
        assertEquals(prepTextDuelGame.getQuizType(), foundGame.getQuizType());
        assertEquals(prepTextDuelGame.getModeType(), foundGame.getModeType());
    }

    @Test
    public void createGame_validInput_success() {
        Game createdGame = gameService.createGame(workingTextDuelGame.getInvitingUserId(), workingTextDuelGame.getInvitedUserId(), workingTextDuelGame.getQuizType(), workingTextDuelGame.getModeType());

        assertNotNull(gameService.getGame(createdGame.getId()));

        assertEquals(workingTextDuelGame.getInvitedUserId(), createdGame.getInvitedUserId());
        assertEquals(workingTextDuelGame.getInvitingUserId(), createdGame.getInvitingUserId());
        assertEquals(workingTextDuelGame.getQuizType(), createdGame.getQuizType());
        assertEquals(workingTextDuelGame.getModeType(), createdGame.getModeType());
    }

    @Test
    public void removeGame_validInput_success() {
        gameService.removeGame(prepTextDuelGame.getId());
        assertNull(gameService.getGame(prepTextDuelGame.getId()));
    }

    @Test
    public void removeGame_nonValidInput_noChange() {
        assertNotNull(gameService.getGame(prepTextDuelGame.getId()));
        gameService.removeGame(workingTextDuelGame.getId());
        assertNotNull(gameService.getGame(prepTextDuelGame.getId()));
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
        Map<String, List<Category>> topicsMap = gameService.getRandomTopics(prepTextDuelGame.getId(), prepTextDuelGame.getInvitedUserId());
        List<Category> topicsList = topicsMap.get("topics");

        assertEquals(3, topicsList.size());
    }

    @Test
    public void getRandomTopics_rotating_success() {
        int counterInvited = 0;
        int counterInviting = 0;

        for (int i = 0; i < 5; i++) {
            Map<String, List<Category>> topicsMap = gameService.getRandomTopics(prepTextDuelGame.getId(), prepTextDuelGame.getInvitedUserId());
            List<Category> topicsList = topicsMap.get("topics");
            assertEquals(3, topicsList.size());
            counterInvited += 1;

            topicsMap = gameService.getRandomTopics(prepTextDuelGame.getId(), prepTextDuelGame.getInvitingUserId());
            topicsList = topicsMap.get("topics");
            assertEquals(3, topicsList.size());
            counterInviting += 1;
        }
        assertEquals(counterInvited, counterInviting);
    }

    @Test
    public void getRandomTopics_requestedTwice_throwsException() {
        Map<String, List<Category>> topicsMap = gameService.getRandomTopics(prepTextDuelGame.getId(), prepTextDuelGame.getInvitedUserId());
        List<Category> topicsList = topicsMap.get("topics");

        assertEquals(3, topicsList.size());
        assertThrows(ResponseStatusException.class, () -> {
            gameService.getRandomTopics(prepTextDuelGame.getId(), prepTextDuelGame.getInvitedUserId());
        });
    }

    @Test
    public void checkGame_gameExists() {
        gameService.checkGame(prepTextDuelGame.getId());
    }

    @Test
    public void checkGame_gameNotExists_exceptionRaised() {
        assertThrows(ResponseStatusException.class, () -> {
            gameService.checkGame(workingTextDuelGame.getId());
        });
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

        Question receivedQuestion = gameService.getQuestion(Category.MUSIC, prepTextDuelGame.getId());
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
            Question receivedQuestion = gameService.getQuestion(category, prepTextDuelGame.getId());
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

        assertThrows(ResponseStatusException.class, () -> {
            gameService.getQuestion(Category.MUSIC, prepTextDuelGame.getId());
        });
    }

    @Test
    public void answerQuestion_bothCorrect_success() {
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
        String[] allAnswers = {"Neil Young", "Bob Dylan", "Eric Clapton", "Elton John"};

        Question createdQuestion = new Question();
        createdQuestion.setCategory(Category.MUSIC);
        createdQuestion.setId("622a1c357cc59eab6f94ff56");
        createdQuestion.setCorrectAnswer("Bob Dylan");
        createdQuestion.setIncorrectAnswers(incorrectAnswers);
        createdQuestion.setAllAnswers(allAnswers);
        createdQuestion.setQuestion("Which musician has famously performed over 3,000 shows in their 'Never Ending Tour'?");

        Game game = gameService.getGame(prepTextDuelGame.getId());
        game.addQuestion(createdQuestion);

        given(userService.searchUserById(invitingUser.getId())).willReturn(invitingUser);
        given(userService.searchUserById(invitedUser.getId())).willReturn(invitedUser);

        UserAnswerTuple invitingUserAnswerTuple = new UserAnswerTuple(invitingUser.getId(), createdQuestion.getId(), createdQuestion.getCorrectAnswer(), 200L);
        UserAnswerTuple invitedUserAnswerTuple = new UserAnswerTuple(invitedUser.getId(), createdQuestion.getId(), createdQuestion.getCorrectAnswer(), 300L);

        Map<String, Boolean> invitingBoolMap = gameService.answerQuestion(prepTextDuelGame.getId(), invitingUserAnswerTuple, webSocketController);
        Map<String, Boolean> invitedBoolMap = gameService.answerQuestion(prepTextDuelGame.getId(), invitedUserAnswerTuple, webSocketController);


        assertEquals(invitingUserAnswerTuple.getAnswer().equals(createdQuestion.getCorrectAnswer()), invitingBoolMap.get("boolean"));
        assertEquals(invitedUserAnswerTuple.getAnswer().equals(createdQuestion.getCorrectAnswer()), invitedBoolMap.get("boolean"));
    }

    @Test
    public void answerQuestion_firstFalse_success() {
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
        String[] allAnswers = {"Neil Young", "Bob Dylan", "Eric Clapton", "Elton John"};

        Question createdQuestion = new Question();
        createdQuestion.setCategory(Category.MUSIC);
        createdQuestion.setId("622a1c357cc59eab6f94ff56");
        createdQuestion.setCorrectAnswer("Bob Dylan");
        createdQuestion.setIncorrectAnswers(incorrectAnswers);
        createdQuestion.setAllAnswers(allAnswers);
        createdQuestion.setQuestion("Which musician has famously performed over 3,000 shows in their 'Never Ending Tour'?");

        Game game = gameService.getGame(prepTextDuelGame.getId());
        game.addQuestion(createdQuestion);

        given(userService.searchUserById(invitingUser.getId())).willReturn(invitingUser);
        given(userService.searchUserById(invitedUser.getId())).willReturn(invitedUser);

        UserAnswerTuple invitingUserAnswerTuple = new UserAnswerTuple(invitingUser.getId(), createdQuestion.getId(), createdQuestion.getIncorrectAnswers()[1], 200L);
        UserAnswerTuple invitedUserAnswerTuple = new UserAnswerTuple(invitedUser.getId(), createdQuestion.getId(), createdQuestion.getCorrectAnswer(), 300L);

        Map<String, Boolean> invitingBoolMap = gameService.answerQuestion(prepTextDuelGame.getId(), invitingUserAnswerTuple, webSocketController);
        Map<String, Boolean> invitedBoolMap = gameService.answerQuestion(prepTextDuelGame.getId(), invitedUserAnswerTuple, webSocketController);


        assertEquals(invitingUserAnswerTuple.getAnswer().equals(createdQuestion.getCorrectAnswer()), invitingBoolMap.get("boolean"));
        assertEquals(invitedUserAnswerTuple.getAnswer().equals(createdQuestion.getCorrectAnswer()), invitedBoolMap.get("boolean"));
    }

    @Test
    public void finishGame_success() {
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
        String[] allAnswers = {"Neil Young", "Bob Dylan", "Eric Clapton", "Elton John"};

        Question createdQuestion = new Question();
        createdQuestion.setCategory(Category.MUSIC);
        createdQuestion.setId("622a1c357cc59eab6f94ff56");
        createdQuestion.setCorrectAnswer("Bob Dylan");
        createdQuestion.setIncorrectAnswers(incorrectAnswers);
        createdQuestion.setAllAnswers(allAnswers);
        createdQuestion.setQuestion("Which musician has famously performed over 3,000 shows in their 'Never Ending Tour'?");

        Game game = gameService.getGame(prepTextDuelGame.getId());

        game.addQuestion(createdQuestion);

        given(userService.searchUserById(invitingUser.getId())).willReturn(invitingUser);
        given(userService.searchUserById(invitedUser.getId())).willReturn(invitedUser);

        UserAnswerTuple invitingUserAnswerTuple = new UserAnswerTuple(invitingUser.getId(), createdQuestion.getId(), createdQuestion.getCorrectAnswer(), 200L);
        UserAnswerTuple invitedUserAnswerTuple = new UserAnswerTuple(invitedUser.getId(), createdQuestion.getId(), createdQuestion.getCorrectAnswer(), 300L);
        gameService.answerQuestion(prepTextDuelGame.getId(), invitingUserAnswerTuple, webSocketController);
        gameService.answerQuestion(prepTextDuelGame.getId(), invitedUserAnswerTuple, webSocketController);

        UserResultTuple finalResult = gameService.finishGame(game.getId());

        assertEquals(prepTextDuelGame.getId(), finalResult.getGameId());
        assertEquals(prepTextDuelGame.getInvitingUserId(), finalResult.getInvitingPlayerId());
        assertEquals((500L - (0.5 * invitingUserAnswerTuple.getAnsweredTime())), finalResult.getInvitingPlayerResult());
        assertEquals(prepTextDuelGame.getInvitedUserId(), finalResult.getInvitedPlayerId());
        assertEquals((500L - (0.5 * invitedUserAnswerTuple.getAnsweredTime())) , finalResult.getInvitedPlayerResult());

        assertNull(gameService.getGame(game.getId()));
    }

    @Test
    public void answerQuestion_nullAnswer_throwsException() {
        assertThrows(ResponseStatusException.class, () -> {
            gameService.answerQuestion(prepTextDuelGame.getId(), null, webSocketController);
        });
    }
}