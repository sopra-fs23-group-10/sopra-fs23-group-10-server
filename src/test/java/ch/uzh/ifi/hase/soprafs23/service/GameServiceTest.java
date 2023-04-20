package ch.uzh.ifi.hase.soprafs23.service;


import ch.uzh.ifi.hase.soprafs23.constant.Category;
import ch.uzh.ifi.hase.soprafs23.constant.ModeType;
import ch.uzh.ifi.hase.soprafs23.constant.QuizType;
import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.handler.GameMap;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserResultTupleDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GameServiceTest {

    @InjectMocks
    private GameService gameService;

    @Mock
    private UserService userService;

    private Game prepTextDuelGame;

    private Game workingTextDuelGame;

    private User invitingUser;

    private User invitedUser;

    private Question createdQuestion;

    private final String[] incorrectAnswers = {"Neil Young", "Eric Clapton", "Elton John"};
    private final String[] allAnswers = {"Neil Young", "Bob Dylan", "Eric Clapton", "Elton John"};


    @BeforeEach
    public void setup() {
        prepTextDuelGame = new Game(0L, 1L, 2L, QuizType.TEXT, ModeType.DUEL);
        workingTextDuelGame = new Game(1L, 3L, 4L, QuizType.TEXT, ModeType.DUEL);

        MockitoAnnotations.openMocks(this);
        GameMap gameMap = mock(GameMap.class);

        gameMap.put(prepTextDuelGame);

        invitingUser = new User();
        invitingUser.setId(1L);
        invitingUser.setStatus(UserStatus.ONLINE);

        invitedUser = new User();
        invitedUser.setId(2L);
        invitedUser.setStatus(UserStatus.ONLINE);

        createdQuestion = new Question();
        createdQuestion.setCategory(Category.MUSIC);
        createdQuestion.setId("622a1c357cc59eab6f94ff56");
        createdQuestion.setCorrectAnswer("Bob Dylan");
        createdQuestion.setIncorrectAnswers(incorrectAnswers);
        createdQuestion.setAllAnswers(allAnswers);
        createdQuestion.setQuestion("Which musician has famously performed over 3,000 shows in their 'Never Ending Tour'?");
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
        gameService.createGame(prepTextDuelGame.getInvitingUserId(), prepTextDuelGame.getInvitedUserId(), prepTextDuelGame.getQuizType(), prepTextDuelGame.getModeType());

        Game foundGame = gameService.getGame(prepTextDuelGame.getId());

        assertNotNull(foundGame);
        assertEquals(prepTextDuelGame.getId(), foundGame.getId());
        assertEquals(prepTextDuelGame.getInvitedUserId(), foundGame.getInvitedUserId());
        assertEquals(prepTextDuelGame.getInvitingUserId(), foundGame.getInvitingUserId());
        assertEquals(prepTextDuelGame.getQuizType(), foundGame.getQuizType());
        assertEquals(prepTextDuelGame.getModeType(), foundGame.getModeType());
    }

    @Test
    public void getGame_gameNotExists_exceptionRaised() {
        assertThrows(ResponseStatusException.class, () -> {
            gameService.getGame(workingTextDuelGame.getId());
        });
        assertThrows(ResponseStatusException.class, () -> {
            gameService.getGame(prepTextDuelGame.getId());
        });
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
        gameService.createGame(prepTextDuelGame.getInvitingUserId(), prepTextDuelGame.getInvitedUserId(), prepTextDuelGame.getQuizType(), prepTextDuelGame.getModeType());

        assertNotNull(gameService.getGame(prepTextDuelGame.getId()));
        gameService.removeGame(prepTextDuelGame.getId());
        assertThrows(ResponseStatusException.class, () -> {
            assertNull(gameService.getGame(prepTextDuelGame.getId()));
        });
    }

    @Test
    public void removeGame_nonValidInput_noChange() {
        gameService.createGame(prepTextDuelGame.getInvitingUserId(), prepTextDuelGame.getInvitedUserId(), prepTextDuelGame.getQuizType(), prepTextDuelGame.getModeType());

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
        gameService.createGame(prepTextDuelGame.getInvitingUserId(), prepTextDuelGame.getInvitedUserId(), prepTextDuelGame.getQuizType(), prepTextDuelGame.getModeType());

        Map<String, List<Category>> topicsMap = gameService.getRandomTopics(prepTextDuelGame.getId(), prepTextDuelGame.getInvitedUserId());
        List<Category> topicsList = topicsMap.get("topics");

        assertEquals(3, topicsList.size());
    }

    @Test
    public void getRandomTopics_rotating_success() {
        gameService.createGame(prepTextDuelGame.getInvitingUserId(), prepTextDuelGame.getInvitedUserId(), prepTextDuelGame.getQuizType(), prepTextDuelGame.getModeType());

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
        gameService.createGame(prepTextDuelGame.getInvitingUserId(), prepTextDuelGame.getInvitedUserId(), prepTextDuelGame.getQuizType(), prepTextDuelGame.getModeType());

        Map<String, List<Category>> topicsMap = gameService.getRandomTopics(prepTextDuelGame.getId(), prepTextDuelGame.getInvitedUserId());
        List<Category> topicsList = topicsMap.get("topics");

        assertEquals(3, topicsList.size());
        assertThrows(ResponseStatusException.class, () -> {
            gameService.getRandomTopics(prepTextDuelGame.getId(), prepTextDuelGame.getInvitedUserId());
        });
    }

    @Test
    public void getQuestion_validInput_success() {
        gameService.createGame(prepTextDuelGame.getInvitingUserId(), prepTextDuelGame.getInvitedUserId(), prepTextDuelGame.getQuizType(), prepTextDuelGame.getModeType());

        User user = new User();
        user.setId(1L);

        when(userService.searchUserById(Mockito.any())).thenReturn(user);

        Category givenCategory = Category.GEOGRAPHY;

        Question receivedQuestion = gameService.getQuestion(Category.GEOGRAPHY, prepTextDuelGame.getId());
        assertNotNull(receivedQuestion);
        assertEquals(receivedQuestion.getClass(), Question.class);
        assertEquals(givenCategory, receivedQuestion.getCategory());
    }

    @Test
    public void getQuestion_checkTopics_success() {
        gameService.createGame(prepTextDuelGame.getInvitingUserId(), prepTextDuelGame.getInvitedUserId(), prepTextDuelGame.getQuizType(), prepTextDuelGame.getModeType());

        when(userService.searchUserById(Mockito.any())).thenReturn(new User());

        List<Category> allTopics = new ArrayList<>(Arrays.asList(Category.values()));

        for (Category category : allTopics) {
            Question receivedQuestion = gameService.getQuestion(category, prepTextDuelGame.getId());
            assertNotNull(receivedQuestion);
            assertEquals(category, receivedQuestion.getCategory());
        }
    }

    @Test
    public void getQuestion_invalidID_throwsException() {
        gameService.createGame(prepTextDuelGame.getInvitingUserId(), prepTextDuelGame.getInvitedUserId(), prepTextDuelGame.getQuizType(), prepTextDuelGame.getModeType());

        when(userService.searchUserById(Mockito.any())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User with specified ID does not exist."));

        assertThrows(ResponseStatusException.class, () -> {
            gameService.getQuestion(Category.MUSIC, prepTextDuelGame.getId());
        });
    }

    @Test
    public void answerQuestion_bothCorrect_success() {
        gameService.createGame(prepTextDuelGame.getInvitingUserId(), prepTextDuelGame.getInvitedUserId(), prepTextDuelGame.getQuizType(), prepTextDuelGame.getModeType());
        gameService.getGame(prepTextDuelGame.getId()).addQuestion(createdQuestion);

        given(userService.searchUserById(invitingUser.getId())).willReturn(invitingUser);
        given(userService.searchUserById(invitedUser.getId())).willReturn(invitedUser);

        UserAnswerTuple invitingUserAnswerTuple = new UserAnswerTuple(invitingUser.getId(), createdQuestion.getId(), createdQuestion.getCorrectAnswer(), 200L);
        UserAnswerTuple invitedUserAnswerTuple = new UserAnswerTuple(invitedUser.getId(), createdQuestion.getId(), createdQuestion.getCorrectAnswer(), 300L);

        assertFalse(createdQuestion.completelyAnswered());
        gameService.answerQuestion(prepTextDuelGame.getId(), invitingUserAnswerTuple);
        assertFalse(createdQuestion.completelyAnswered());
        gameService.answerQuestion(prepTextDuelGame.getId(), invitedUserAnswerTuple);
        assertTrue(createdQuestion.completelyAnswered());

        Map<Long, UserAnswerTuple> results = createdQuestion.getResults();

        assertEquals(invitingUserAnswerTuple.getUserId(), results.get(invitingUser.getId()).getUserId());
        assertEquals(invitingUserAnswerTuple.getQuestionId(), results.get(invitingUser.getId()).getQuestionId());
        assertEquals(invitingUserAnswerTuple.getAnswer(), results.get(invitingUser.getId()).getAnswer());
        assertEquals(invitingUserAnswerTuple.getAnsweredTime(), results.get(invitingUser.getId()).getAnsweredTime());
        assertEquals(invitedUserAnswerTuple.getUserId(), results.get(invitedUser.getId()).getUserId());
        assertEquals(invitedUserAnswerTuple.getQuestionId(), results.get(invitedUser.getId()).getQuestionId());
        assertEquals(invitedUserAnswerTuple.getAnswer(), results.get(invitedUser.getId()).getAnswer());
        assertEquals(invitedUserAnswerTuple.getAnsweredTime(), results.get(invitedUser.getId()).getAnsweredTime());
    }

    @Test
    public void answerQuestion_nullAnswer_throwsException() {
        gameService.removeGame(prepTextDuelGame.getId());

        assertThrows(ResponseStatusException.class, () -> {
            gameService.answerQuestion(prepTextDuelGame.getId(), null);
        });
    }

    @Test
    public void finishGame_success() {
        gameService.createGame(prepTextDuelGame.getInvitingUserId(), prepTextDuelGame.getInvitedUserId(), prepTextDuelGame.getQuizType(), prepTextDuelGame.getModeType());
        gameService.getGame(prepTextDuelGame.getId()).addQuestion(createdQuestion);

        given(userService.searchUserById(invitingUser.getId())).willReturn(invitingUser);
        given(userService.searchUserById(invitedUser.getId())).willReturn(invitedUser);

        UserAnswerTuple invitingUserAnswerTuple = new UserAnswerTuple(invitingUser.getId(), createdQuestion.getId(), createdQuestion.getIncorrectAnswers()[1], 200L);
        UserAnswerTuple invitedUserAnswerTuple = new UserAnswerTuple(invitedUser.getId(), createdQuestion.getId(), createdQuestion.getCorrectAnswer(), 300L);

        gameService.answerQuestion(prepTextDuelGame.getId(), invitingUserAnswerTuple);
        gameService.answerQuestion(prepTextDuelGame.getId(), invitedUserAnswerTuple);

        List<UserResultTupleDTO> finalResult = gameService.finishGame(prepTextDuelGame.getId());

        for (UserResultTupleDTO userResultTupleDTO : finalResult) {
            assertEquals(prepTextDuelGame.getId(), userResultTupleDTO.getGameId());
            assertEquals(invitingUser.getId(), userResultTupleDTO.getInvitingPlayerId());
            assertEquals(0L, userResultTupleDTO.getInvitingPlayerResult());
            assertEquals(prepTextDuelGame.getInvitedUserId(), invitedUserAnswerTuple.getUserId());
            assertEquals(350L, userResultTupleDTO.getInvitedPlayerResult());
        }

        assertThrows(ResponseStatusException.class, () -> {
            assertNull(gameService.getGame(prepTextDuelGame.getId()));
        });
    }

    @Test
    public void intermediateResults_success() {
        gameService.createGame(prepTextDuelGame.getInvitingUserId(), prepTextDuelGame.getInvitedUserId(), prepTextDuelGame.getQuizType(), prepTextDuelGame.getModeType());

        gameService.getGame(prepTextDuelGame.getId()).addQuestion(createdQuestion);

        given(userService.searchUserById(invitingUser.getId())).willReturn(invitingUser);
        given(userService.searchUserById(invitedUser.getId())).willReturn(invitedUser);

        UserAnswerTuple invitingUserAnswerTuple = new UserAnswerTuple(invitingUser.getId(), createdQuestion.getId(), createdQuestion.getIncorrectAnswers()[1], 200L);
        UserAnswerTuple invitedUserAnswerTuple = new UserAnswerTuple(invitedUser.getId(), createdQuestion.getId(), createdQuestion.getCorrectAnswer(), 300L);

        gameService.answerQuestion(prepTextDuelGame.getId(), invitingUserAnswerTuple);
        gameService.answerQuestion(prepTextDuelGame.getId(), invitedUserAnswerTuple);

        List<UserResultTupleDTO> intermediateResult = gameService.intermediateResults(prepTextDuelGame.getId());

        for (UserResultTupleDTO userResultTupleDTO : intermediateResult) {
            assertEquals(prepTextDuelGame.getId(), userResultTupleDTO.getGameId());
            assertEquals(invitingUser.getId(), userResultTupleDTO.getInvitingPlayerId());
            assertEquals(0L, userResultTupleDTO.getInvitingPlayerResult());
            assertEquals(prepTextDuelGame.getInvitedUserId(), invitedUserAnswerTuple.getUserId());
            assertEquals(350L, userResultTupleDTO.getInvitedPlayerResult());
        }
        assertNotNull(gameService.getGame(prepTextDuelGame.getId()));
    }
}