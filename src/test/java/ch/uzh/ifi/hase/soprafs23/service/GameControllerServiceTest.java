package ch.uzh.ifi.hase.soprafs23.service;


import ch.uzh.ifi.hase.soprafs23.constant.Category;
import ch.uzh.ifi.hase.soprafs23.constant.ModeType;
import ch.uzh.ifi.hase.soprafs23.constant.QuizType;
import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserResultTupleDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.verify;

class GameControllerServiceTest {

    @InjectMocks
    private GameControllerService gameControllerService;

    @Mock
    private UserService userService;
    @Mock
    private GameService gameService;
    @Mock
    private QuestionService questionService;
    @Mock
    private AnswerService answerService;

    private Game prepTextDuelGame;

    private Game workingTextDuelGame;

    private User invitingUser;

    private User invitedUser;

    private Question createdQuestion;

    private Answer correctAnswer;

    private Random random = new Random();

    private final List<String> incorrectAnswers = Arrays.asList("Neil Young", "Eric Clapton", "Elton John");
    private final List<String> allAnswers = Arrays.asList("Neil Young", "Bob Dylan", "Eric Clapton", "Elton John");


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        invitingUser = new User();
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

        workingTextDuelGame = new Game();
        workingTextDuelGame.setGameId(1L);
        workingTextDuelGame.setInvitingUserId(3L);
        workingTextDuelGame.setInvitedUserId(4L);
        workingTextDuelGame.setQuizType(QuizType.TEXT);
        workingTextDuelGame.setModeType(ModeType.DUEL);
        workingTextDuelGame.setCurrentPlayer(invitedUser.getId());

        createdQuestion = new Question();
        createdQuestion.setGameId(prepTextDuelGame.getGameId());
        createdQuestion.setQuestionId(1L);
        createdQuestion.setCategory(Category.MUSIC);
        createdQuestion.setApiId("622a1c357cc59eab6f94ff56");
        createdQuestion.setCorrectAnswer("Bob Dylan");
        createdQuestion.setIncorrectAnswers(incorrectAnswers);
        createdQuestion.setAllAnswers(allAnswers);
        createdQuestion.setQuestion("Which musician has famously performed over 3,000 shows in their 'Never Ending Tour'?");
        createdQuestion.setCreationTime(new Date());

        correctAnswer = new Answer();
        correctAnswer.setAnswer(createdQuestion.getCorrectAnswer());
        correctAnswer.setAnsweredTime(10000L);
        correctAnswer.setId(createdQuestion.getQuestionId());
        correctAnswer.setUserId(invitedUser.getId());
    }

    /*
    @AfterEach
    public void resetGameMap() throws Exception {
        Field instance = GameMap.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }*/

    @Test
    void searchGame_validInput_success() {
        given(gameService.searchGameById(prepTextDuelGame.getGameId())).willReturn(prepTextDuelGame);

        Game foundGame = gameControllerService.searchGame(prepTextDuelGame.getGameId());

        assertNotNull(foundGame);
        assertEquals(prepTextDuelGame.getGameId(), foundGame.getGameId());
        assertEquals(prepTextDuelGame.getInvitedUserId(), foundGame.getInvitedUserId());
        assertEquals(prepTextDuelGame.getInvitingUserId(), foundGame.getInvitingUserId());
        assertEquals(prepTextDuelGame.getQuizType(), foundGame.getQuizType());
        assertEquals(prepTextDuelGame.getModeType(), foundGame.getModeType());
    }

    @Test
    void searchGame_gameNotExists_exceptionRaised() {
        given(gameService.searchGameById(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Game with specified ID cannot be found."));
        assertThrows(ResponseStatusException.class, () -> gameControllerService.searchGame(workingTextDuelGame.getGameId()));
        assertThrows(ResponseStatusException.class, () -> gameControllerService.searchGame(prepTextDuelGame.getGameId()));
    }

    @Test
    void createGame_validInput_success() {
        given(gameService.createGame(prepTextDuelGame.getInvitingUserId(), prepTextDuelGame.getInvitedUserId(), prepTextDuelGame.getQuizType(), prepTextDuelGame.getModeType())).willReturn(prepTextDuelGame);

        Game createdGame = gameControllerService.createGame(prepTextDuelGame.getInvitingUserId(), prepTextDuelGame.getInvitedUserId(), prepTextDuelGame.getQuizType(), prepTextDuelGame.getModeType());

        given(gameService.searchGameById(createdGame.getGameId())).willReturn(createdGame);

        assertNotNull(gameControllerService.searchGame(createdGame.getGameId()));

        assertEquals(prepTextDuelGame.getInvitedUserId(), createdGame.getInvitedUserId());
        assertEquals(prepTextDuelGame.getInvitingUserId(), createdGame.getInvitingUserId());
        assertEquals(prepTextDuelGame.getQuizType(), createdGame.getQuizType());
        assertEquals(prepTextDuelGame.getModeType(), createdGame.getModeType());
    }

    @Test
    void removeGame_validInput_success() {

        given(gameService.searchGameById(prepTextDuelGame.getGameId())).willReturn(prepTextDuelGame);
        given(questionService.searchQuestionsByGameId(prepTextDuelGame.getGameId())).willReturn(Arrays.asList(createdQuestion));

        gameControllerService.removeGame(prepTextDuelGame.getGameId());

        verify(gameService).searchGameById(prepTextDuelGame.getGameId());
        verify(userService).setOnline(invitedUser.getId());
        verify(userService).setOnline(invitingUser.getId());
        verify(answerService).deleteAnswers(createdQuestion.getQuestionId());
        verify(questionService).deleteQuestions(prepTextDuelGame.getGameId());
        verify(gameService).deleteGame(prepTextDuelGame.getGameId());
    }



    @Test
    void removeGame_nonValidInput_noChange() {
        given(questionService.searchQuestionsByGameId(prepTextDuelGame.getGameId())).willReturn(Arrays.asList(createdQuestion));
        given(gameService.searchGameById(prepTextDuelGame.getGameId())).willReturn(prepTextDuelGame);

        assertNotNull(gameControllerService.searchGame(prepTextDuelGame.getGameId()));

        gameControllerService.removeGame(prepTextDuelGame.getGameId());

        assertNotNull(gameControllerService.searchGame(prepTextDuelGame.getGameId()));
    }



    @Test
    void getAllTopics_success() {
        Map<String, List<Category>> topicsMap = gameControllerService.getAllTopics();
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
    void getRandomTopics_once_success() {
        given(gameService.searchGameById(prepTextDuelGame.getGameId())).willReturn(prepTextDuelGame);

        Map<String, List<Category>> topicsMap = gameControllerService.getRandomTopics(prepTextDuelGame.getGameId(), prepTextDuelGame.getInvitedUserId());
        prepTextDuelGame.setCurrentPlayer(invitingUser.getId());
        List<Category> topicsList = topicsMap.get("topics");

        assertEquals(3, topicsList.size());
    }

    @Test
    void getRandomTopics_rotating_success() {
        given(gameService.searchGameById(prepTextDuelGame.getGameId())).willReturn(prepTextDuelGame);

        int counterInvited = 0;
        int counterInviting = 0;

        for (int i = 0; i < 5; i++) {
            Map<String, List<Category>> topicsMap = gameControllerService.getRandomTopics(prepTextDuelGame.getGameId(), prepTextDuelGame.getInvitedUserId());
            prepTextDuelGame.setCurrentPlayer(invitingUser.getId());
            List<Category> topicsList = topicsMap.get("topics");
            assertEquals(3, topicsList.size());
            counterInvited += 1;

            topicsMap = gameControllerService.getRandomTopics(prepTextDuelGame.getGameId(), prepTextDuelGame.getInvitingUserId());
            prepTextDuelGame.setCurrentPlayer(invitedUser.getId());
            topicsList = topicsMap.get("topics");
            assertEquals(3, topicsList.size());
            counterInviting += 1;
        }
        assertEquals(counterInvited, counterInviting);
    }

    /*
    @Test
    void getRandomTopics_requestedTwice_throwsException() {
        given(gameService.searchGameById(prepTextDuelGame.getGameId())).willReturn(prepTextDuelGame);

        Map<String, List<Category>> topicsMap = gameControllerService.getRandomTopics(prepTextDuelGame.getGameId(), prepTextDuelGame.getInvitedUserId());
        prepTextDuelGame.setCurrentPlayer(invitingUser.getId());
        List<Category> topicsList = topicsMap.get("topics");

        assertEquals(3, topicsList.size());
        assertThrows(ResponseStatusException.class, () -> gameControllerService.getRandomTopics(prepTextDuelGame.getGameId(), prepTextDuelGame.getInvitedUserId()));
    }


    @Test
    void getQuestion_validInput_success() {
        given(gameService.searchGameById(prepTextDuelGame.getGameId())).willReturn(prepTextDuelGame);
        given(questionService.createQuestion(Mockito.any(Long.class), Mockito.any(String.class), Mockito.any(Category.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(List.class))).willCallRealMethod();

        when(userService.searchUserById(invitingUser.getId())).thenReturn(invitingUser);
        when(userService.searchUserById(invitedUser.getId())).thenReturn(invitedUser);

        Category givenCategory = Category.FILM_TV;

        Question receivedQuestion = gameControllerService.getQuestion(givenCategory, prepTextDuelGame.getGameId());
        assertNotNull(receivedQuestion);
        assertEquals(receivedQuestion.getClass(), Question.class);
        assertEquals(givenCategory, receivedQuestion.getCategory());
    }

    @Test
    public void getQuestion_checkTopics_success() {
        gameControllerService.createGame(prepTextDuelGame.getInvitingUserId(), prepTextDuelGame.getInvitedUserId(), prepTextDuelGame.getQuizType(), prepTextDuelGame.getModeType());

        when(userService.searchUserById(Mockito.any())).thenReturn(new User());

        List<Category> allTopics = new ArrayList<>(Arrays.asList(Category.values()));

        for (Category category : allTopics) {
            Question receivedQuestion = gameControllerService.getQuestion(category, prepTextDuelGame.getGameId());
            assertNotNull(receivedQuestion);
            assertEquals(category, receivedQuestion.getCategory());
        }
    }

    @Test
    public void getQuestion_invalidID_throwsException() {
        gameControllerService.createGame(prepTextDuelGame.getInvitingUserId(), prepTextDuelGame.getInvitedUserId(), prepTextDuelGame.getQuizType(), prepTextDuelGame.getModeType());

        when(userService.searchUserById(Mockito.any())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User with specified ID does not exist."));

        assertThrows(ResponseStatusException.class, () -> {
            gameControllerService.getQuestion(Category.MUSIC, prepTextDuelGame.getGameId());
        });
    }

  */
/*
    @Test
    public void answerQuestion_bothCorrect_success() {
        given(gameService.searchGameById(prepTextDuelGame.getGameId())).willReturn(prepTextDuelGame);

        given(userService.searchUserById(invitingUser.getId())).willReturn(invitingUser);
        given(userService.searchUserById(invitedUser.getId())).willReturn(invitedUser);

        Answer invitingAnswer = new Answer();
        invitingAnswer.setId(1L);
        invitingAnswer.setQuestionId(createdQuestion.getQuestionId());
        invitingAnswer.setUserId(invitingUser.getId());
        invitingAnswer.setAnswer(createdQuestion.getCorrectAnswer());
        invitingAnswer.setAnsweredTime(200L);

        Answer invitedAnswer = new Answer();
        invitedAnswer.setId(2L);
        invitedAnswer.setQuestionId(createdQuestion.getQuestionId());
        invitedAnswer.setUserId(invitedUser.getId());
        invitedAnswer.setAnswer(createdQuestion.getCorrectAnswer());
        invitedAnswer.setAnsweredTime(300L);

        given(answerService.searchAnswerByQuestionIdAndUserId(Mockito.any(), Mockito.any())).willReturn(null);
        given(questionService.searchQuestionByQuestionId(Mockito.any())).willReturn(createdQuestion);

        gameControllerService.answerQuestion(prepTextDuelGame.getGameId(), invitingAnswer);
        gameControllerService.answerQuestion(prepTextDuelGame.getGameId(), invitedAnswer);

        assertEquals(invitingAnswer.getUserId(), results.get(invitingUser.getId()).getUserId());
        assertEquals(invitingAnswer.getQuestionId(), results.get(invitingUser.getId()).getQuestionId());
        assertEquals(invitingAnswer.getAnswer(), results.get(invitingUser.getId()).getAnswer());
        assertEquals(invitingAnswer.getAnsweredTime(), results.get(invitingUser.getId()).getAnsweredTime());
        assertEquals(invitedAnswer.getUserId(), results.get(invitedUser.getId()).getUserId());
        assertEquals(invitedAnswer.getQuestionId(), results.get(invitedUser.getId()).getQuestionId());
        assertEquals(invitedAnswer.getAnswer(), results.get(invitedUser.getId()).getAnswer());
        assertEquals(invitedAnswer.getAnsweredTime(), results.get(invitedUser.getId()).getAnsweredTime());
    }*/

    @Test
    void answerQuestion_nullAnswer_throwsException() {
        assertThrows(ResponseStatusException.class, () -> gameControllerService.answerQuestion(null));
    }

    @Test
    void answerQuestion_correctAnswer_TimeRunUp() {
        given(answerService.searchAnswerByQuestionIdAndUserId(createdQuestion.getGameId(), invitedUser.getId())).willReturn(null);
        given(questionService.searchQuestionByQuestionId(createdQuestion.getQuestionId())).willReturn(createdQuestion);

        Answer answer = new Answer();
        answer.setAnswer("CorrectAnswer");
        answer.setAnsweredTime(3L);
        answer.setQuestionId(1L);
        answer.setUserId(3L);

        createdQuestion.setCreationTime(new Date(createdQuestion.getCreationTime().getTime() - 31000));

        gameControllerService.answerQuestion(answer);
        assertEquals( "WrongAnswer",answer.getAnswer());
    }

    @Test
    void answerQuestion_correctAnswer_TimeRunNotUp() {
        given(answerService.searchAnswerByQuestionIdAndUserId(createdQuestion.getGameId(), invitedUser.getId())).willReturn(null);
        given(questionService.searchQuestionByQuestionId(createdQuestion.getQuestionId())).willReturn(createdQuestion);

        Answer answer = new Answer();
        answer.setAnswer("CorrectAnswer");
        answer.setAnsweredTime(3L);
        answer.setQuestionId(1L);
        answer.setUserId(3L);

        gameControllerService.answerQuestion(answer);
        assertEquals("CorrectAnswer",answer.getAnswer());
    }
/*
    @Test
    public void finishGame_success() {
        given(gameService.searchGameById(prepTextDuelGame.getGameId())).willReturn(prepTextDuelGame);
        given(questionService.deleteQuestions(prepTextDuelGame.getGameId())).willReturn(Arrays.asList(createdQuestion));

        Answer invitingAnswer = new Answer();
        invitingAnswer.setId(1L);
        invitingAnswer.setQuestionId(createdQuestion.getQuestionId());
        invitingAnswer.setUserId(invitingUser.getId());
        invitingAnswer.setAnswer(createdQuestion.getIncorrectAnswers().get(1));
        invitingAnswer.setAnsweredTime(200L);

        Answer invitedAnswer = new Answer();
        invitedAnswer.setId(2L);
        invitedAnswer.setQuestionId(createdQuestion.getQuestionId());
        invitedAnswer.setUserId(invitedUser.getId());
        invitedAnswer.setAnswer(createdQuestion.getCorrectAnswer());
        invitedAnswer.setAnsweredTime(300L);

        List<UserResultTupleDTO> finalResult = gameControllerService.finishGame(prepTextDuelGame.getGameId());

        for (UserResultTupleDTO userResultTupleDTO : finalResult) {
            assertEquals(prepTextDuelGame.getGameId(), userResultTupleDTO.getGameId());
            assertEquals(invitingUser.getId(), userResultTupleDTO.getInvitingPlayerId());
            assertEquals(0L, userResultTupleDTO.getInvitingPlayerResult());
            assertEquals(prepTextDuelGame.getInvitedUserId(), invitedAnswer.getUserId());
            assertEquals(350L, userResultTupleDTO.getInvitedPlayerResult());
        }

        assertThrows(ResponseStatusException.class, () -> {
            assertNull(gameControllerService.searchGame(prepTextDuelGame.getGameId()));
        });
    }
    */

    @Test
    public void intermediateResults_OnlyOneUserAnswered_success() {
        gameControllerService.createGame(prepTextDuelGame.getInvitingUserId(), prepTextDuelGame.getInvitedUserId(), prepTextDuelGame.getQuizType(), prepTextDuelGame.getModeType());

        given(userService.searchUserById(invitingUser.getId())).willReturn(invitingUser);
        given(userService.searchUserById(invitedUser.getId())).willReturn(invitedUser);
        given(gameService.searchGameById(prepTextDuelGame.getGameId())).willReturn(prepTextDuelGame);
        given(questionService.searchQuestionsByGameId(prepTextDuelGame.getGameId())).willReturn(Arrays.asList(createdQuestion));
        given(questionService.searchQuestionByQuestionId(prepTextDuelGame.getGameId())).willReturn(createdQuestion);
        given(answerService.searchAnswerByQuestionIdAndUserId(createdQuestion.getQuestionId(), prepTextDuelGame.getInvitedUserId())).willReturn(correctAnswer);
        given(answerService.searchAnswerByQuestionIdAndUserId(createdQuestion.getQuestionId(), prepTextDuelGame.getInvitingUserId())).willReturn(null);

        gameControllerService.answerQuestion(correctAnswer);
        gameControllerService.intermediateResults(createdQuestion.getGameId());

        List<UserResultTupleDTO> intermediateResult = gameControllerService.intermediateResults(prepTextDuelGame.getGameId());

        for (UserResultTupleDTO userResultTupleDTO : intermediateResult) {
            assertEquals(prepTextDuelGame.getGameId(), userResultTupleDTO.getGameId());
            assertEquals(invitingUser.getId(), userResultTupleDTO.getInvitingPlayerId());
            assertEquals(0L, userResultTupleDTO.getInvitingPlayerResult());
            assertEquals(prepTextDuelGame.getInvitedUserId(), correctAnswer.getUserId());
            assertEquals(500L, userResultTupleDTO.getInvitedPlayerResult());
        }
        assertNotNull(gameControllerService.searchGame(prepTextDuelGame.getGameId()));
    }
}