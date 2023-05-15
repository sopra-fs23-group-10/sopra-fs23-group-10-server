package ch.uzh.ifi.hase.soprafs23.service;


import ch.uzh.ifi.hase.soprafs23.constant.Category;
import ch.uzh.ifi.hase.soprafs23.constant.ModeType;
import ch.uzh.ifi.hase.soprafs23.constant.QuizType;
import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Answer;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Question;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.QuestionDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserResultTupleDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class GameControllerServiceTest {

    @InjectMocks
    @Spy
    private GameControllerService gameControllerService;
    @Mock
    private UserService userService;
    @Mock
    private GameService gameService;
    @Mock
    private QuestionService questionService;
    @Mock
    private AnswerService answerService;

    @Captor
  ArgumentCaptor<Long> gameIdCaptor;
    @Captor
  ArgumentCaptor<String> apiIdCaptor;
    @Captor
  ArgumentCaptor<Category> categoryCaptor;
  @Captor
  ArgumentCaptor<String> correctAnswerCaptor;
  @Captor
  ArgumentCaptor<String> questionCaptor;
  @Captor
  ArgumentCaptor<List<String>> incorrectAnswersCaptor;

    private Game prepTextDuelGame;

    private Game workingTextDuelGame;

    private User invitingUser;

    private User invitedUser;

    private Question createdQuestion;

    private Answer correctAnswer;

    private final List<String> incorrectAnswers = Arrays.asList("Neil Young", "Eric Clapton", "Elton John");
    private final List<String> allAnswers = Arrays.asList("Neil Young", "Bob Dylan", "Eric Clapton", "Elton John");

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        invitingUser = new User();
        invitingUser.setId(1L);
        invitingUser.setStatus(UserStatus.IN_GAME);

        invitedUser = new User();
        invitedUser.setId(2L);
        invitedUser.setStatus(UserStatus.IN_GAME);

        prepTextDuelGame = new Game();
        prepTextDuelGame.setGameId(1L);
        prepTextDuelGame.setInvitingUserId(invitingUser.getId());
        prepTextDuelGame.setInvitedUserId(invitedUser.getId());
        prepTextDuelGame.setQuizType(QuizType.TEXT);
        prepTextDuelGame.setModeType(ModeType.DUEL);
        prepTextDuelGame.setCurrentPlayer(invitedUser.getId());

        workingTextDuelGame = new Game();
        workingTextDuelGame.setGameId(2L);
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
        createdQuestion.setQuestionString("Which musician has famously performed over 3,000 shows in their 'Never Ending Tour'?");
        createdQuestion.setCreationTime(new Date());

        correctAnswer = new Answer();
        correctAnswer.setAnswerString(createdQuestion.getCorrectAnswer());
        correctAnswer.setAnsweredTime(10000L);
        correctAnswer.setId(createdQuestion.getQuestionId());
        correctAnswer.setUserId(invitedUser.getId());
    }

    @Test
    public void createQuestion_returnsQuestionDTOW_LoopTwiceBecauseAlreadyExistent() throws Exception {
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setCategory(createdQuestion.getCategory());
        questionDTO.setGameId(workingTextDuelGame.getGameId());

        given(userService.verifyToken(invitingUser.getToken())).willReturn(invitingUser);
        doReturn(createdQuestion).when(gameControllerService).getQuestionFromExternalApi(createdQuestion.getCategory(), createdQuestion.getGameId());
        given(questionService.existsQuestionByApiIdAndGameId(createdQuestion)).willReturn(true, false);

        gameControllerService.getQuestion(createdQuestion.getCategory(), createdQuestion.getGameId());

        verify(gameControllerService, times(2)).getQuestionFromExternalApi(createdQuestion.getCategory(), createdQuestion.getGameId());
        verify(questionService, times(1)).saveQuestion(createdQuestion);
    }

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
        given(userService.searchUserById(invitingUser.getId())).willReturn(invitingUser);
        given(userService.searchUserById(invitedUser.getId())).willReturn(invitedUser);

        invitedUser.setStatus(UserStatus.IN_GAME);
        invitingUser.setStatus(UserStatus.IN_GAME);

        gameControllerService.setInGamePlayersToOnline(prepTextDuelGame.getGameId());

        verify(gameService).searchGameById(prepTextDuelGame.getGameId());
        verify(userService).setOnline(invitingUser.getId());
        verify(userService).setOnline(invitedUser.getId());
    }



    @Test
    void removeGame_nonValidInput_noChange() {
        given(gameService.searchGameById(prepTextDuelGame.getGameId())).willReturn(prepTextDuelGame);
        given(userService.searchUserById(invitingUser.getId())).willReturn(invitingUser);
        given(userService.searchUserById(invitedUser.getId())).willReturn(invitedUser);

        invitedUser.setStatus(UserStatus.ONLINE);
        invitingUser.setStatus(UserStatus.ONLINE);

        gameControllerService.setInGamePlayersToOnline(prepTextDuelGame.getGameId());

        verify(gameService).searchGameById(prepTextDuelGame.getGameId());
        verify(userService, never()).setOnline(invitingUser.getId());
        verify(userService, never()).setOnline(invitedUser.getId());
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

        Map<String, List<Category>> topicsMap = gameControllerService.getRandomTopics(prepTextDuelGame.getGameId());
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
            Map<String, List<Category>> topicsMap = gameControllerService.getRandomTopics(prepTextDuelGame.getGameId());
            prepTextDuelGame.setCurrentPlayer(invitingUser.getId());
            List<Category> topicsList = topicsMap.get("topics");
            assertEquals(3, topicsList.size());
            counterInvited += 1;

            topicsMap = gameControllerService.getRandomTopics(prepTextDuelGame.getGameId());
            prepTextDuelGame.setCurrentPlayer(invitedUser.getId());
            topicsList = topicsMap.get("topics");
            assertEquals(3, topicsList.size());
            counterInviting += 1;
        }
        assertEquals(counterInvited, counterInviting);
    }

    @Test
    void getQuestionFromExternalApi_validInput_success() {
        given(gameService.searchGameById(prepTextDuelGame.getGameId())).willReturn(prepTextDuelGame);
        given(questionService.createQuestion(Mockito.any(Long.class), Mockito.any(String.class), Mockito.any(Category.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(List.class))).willReturn(new Question());

        when(userService.searchUserById(invitingUser.getId())).thenReturn(invitingUser);
        when(userService.searchUserById(invitedUser.getId())).thenReturn(invitedUser);

        Category givenCategory = Category.FILM_TV;

        gameControllerService.getQuestionFromExternalApi(givenCategory, prepTextDuelGame.getGameId());

        verify(questionService).createQuestion(gameIdCaptor.capture(), apiIdCaptor.capture(), categoryCaptor.capture(), correctAnswerCaptor.capture(), questionCaptor.capture(), incorrectAnswersCaptor.capture());

        assertEquals(prepTextDuelGame.getGameId(), gameIdCaptor.getValue());
        assertEquals(givenCategory, categoryCaptor.getValue());

    }

    @Test
    public void getQuestionFromExternalAPI_checkTopics_success() {
      given(gameService.searchGameById(prepTextDuelGame.getGameId())).willReturn(prepTextDuelGame);
      given(questionService.createQuestion(Mockito.any(Long.class), Mockito.any(String.class), Mockito.any(Category.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(List.class))).willReturn(new Question());

      when(userService.searchUserById(invitingUser.getId())).thenReturn(invitingUser);
      when(userService.searchUserById(invitedUser.getId())).thenReturn(invitedUser);

      List<Category> allTopics = new ArrayList<>(Arrays.asList(Category.values()));

      for (Category category : allTopics) {
        gameControllerService.getQuestionFromExternalApi(category, prepTextDuelGame.getGameId());
        verify(questionService, atLeastOnce()).createQuestion(gameIdCaptor.capture(), apiIdCaptor.capture(), categoryCaptor.capture(), correctAnswerCaptor.capture(), questionCaptor.capture(), incorrectAnswersCaptor.capture());
        assertEquals(prepTextDuelGame.getGameId(), gameIdCaptor.getValue());
        assertEquals(category, categoryCaptor.getValue());
      }
    }

    @Test
    public void getQuestion_everythingValid_success() {
      doReturn(createdQuestion).when(gameControllerService).getQuestionFromExternalApi(Category.MUSIC, prepTextDuelGame.getGameId());
      given(questionService.existsQuestionByApiIdAndGameId(createdQuestion)).willReturn(true);
      given(questionService.saveQuestion(createdQuestion)).willReturn(createdQuestion);

      Question returned = gameControllerService.getQuestion(Category.MUSIC, prepTextDuelGame.getGameId());

      assertEquals(createdQuestion.getQuestionId(), returned.getQuestionId());
      assertEquals(createdQuestion.getQuestionString(), returned.getQuestionString());
      assertEquals(createdQuestion.getGameId(), returned.getGameId());
      assertEquals(createdQuestion.getCategory(), returned.getCategory());
      assertEquals(createdQuestion.getCorrectAnswer(), returned.getCorrectAnswer());
      assertEquals(createdQuestion.getIncorrectAnswers().size(), returned.getIncorrectAnswers().size());
      assertTrue(returned.getIncorrectAnswers().containsAll(createdQuestion.getIncorrectAnswers()));
      assertEquals(createdQuestion.getAllAnswers().size(), returned.getAllAnswers().size());
      assertTrue(returned.getAllAnswers().containsAll(createdQuestion.getAllAnswers()));
    }

    @Test
    public void answerQuestion_bothCorrect_success() {
        given(gameService.searchGameById(prepTextDuelGame.getGameId())).willReturn(prepTextDuelGame);

        given(userService.searchUserById(invitingUser.getId())).willReturn(invitingUser);
        given(userService.searchUserById(invitedUser.getId())).willReturn(invitedUser);

        Answer invitingAnswer = new Answer();
        invitingAnswer.setId(1L);
        invitingAnswer.setQuestionId(createdQuestion.getQuestionId());
        invitingAnswer.setUserId(invitingUser.getId());
        invitingAnswer.setAnswerString(createdQuestion.getCorrectAnswer());
        invitingAnswer.setAnsweredTime(200L);

        Answer invitedAnswer = new Answer();
        invitedAnswer.setId(2L);
        invitedAnswer.setQuestionId(createdQuestion.getQuestionId());
        invitedAnswer.setUserId(invitedUser.getId());
        invitedAnswer.setAnswerString(createdQuestion.getCorrectAnswer());
        invitedAnswer.setAnsweredTime(300L);

      given(answerService.searchAnswerByQuestionIdAndUserId(createdQuestion.getQuestionId(), invitedAnswer.getUserId())).willReturn(null);
      given(answerService.searchAnswerByQuestionIdAndUserId(createdQuestion.getQuestionId(), invitingAnswer.getUserId())).willReturn(null);
        given(questionService.searchQuestionByQuestionId(createdQuestion.getQuestionId())).willReturn(createdQuestion);

      assertEquals(createdQuestion.getCorrectAnswer(), gameControllerService.answerQuestion(invitingAnswer));
      assertEquals(createdQuestion.getCorrectAnswer(), gameControllerService.answerQuestion(invitedAnswer));
    }

    @Test
    void answerQuestion_nullAnswer_throwsException() {
        assertThrows(ResponseStatusException.class, () -> gameControllerService.answerQuestion(null));
    }

    @Test
    void answerQuestion_correctAnswer_TimeRunUp() {
        given(answerService.searchAnswerByQuestionIdAndUserId(createdQuestion.getGameId(), invitedUser.getId())).willReturn(null);
        given(questionService.searchQuestionByQuestionId(createdQuestion.getQuestionId())).willReturn(createdQuestion);

        Answer answer = new Answer();
        answer.setAnswerString("CorrectAnswer");
        answer.setAnsweredTime(3L);
        answer.setQuestionId(1L);
        answer.setUserId(3L);

        createdQuestion.setCreationTime(new Date(createdQuestion.getCreationTime().getTime() - 31000));

        gameControllerService.answerQuestion(answer);
        assertEquals( "WrongAnswer",answer.getAnswerString());
    }

    @Test
    void answerQuestion_correctAnswer_TimeRunNotUp() {
        given(answerService.searchAnswerByQuestionIdAndUserId(createdQuestion.getGameId(), invitedUser.getId())).willReturn(null);
        given(questionService.searchQuestionByQuestionId(createdQuestion.getQuestionId())).willReturn(createdQuestion);

        Answer answer = new Answer();
        answer.setAnswerString("CorrectAnswer");
        answer.setAnsweredTime(3L);
        answer.setQuestionId(1L);
        answer.setUserId(3L);

        gameControllerService.answerQuestion(answer);
        assertEquals("CorrectAnswer",answer.getAnswerString());
    }

    @Test
    void intermediateResults_OnlyOneUserAnswered_success() {

      Answer invitingAnswer = new Answer();
      invitingAnswer.setUserId(invitingUser.getId());
      invitingAnswer.setQuestionId(createdQuestion.getQuestionId());
      invitingAnswer.setAnswerString(createdQuestion.getCorrectAnswer());
      invitingAnswer.setAnsweredTime(10L);

      given(gameService.searchGameById(prepTextDuelGame.getGameId())).willReturn(prepTextDuelGame);
      given(questionService.searchQuestionsByGameId(prepTextDuelGame.getGameId())).willReturn(Arrays.asList(createdQuestion));
      given(answerService.searchAnswerByQuestionIdAndUserId(createdQuestion.getQuestionId(), prepTextDuelGame.getInvitingUserId())). willReturn(invitingAnswer);
      given(answerService.searchAnswerByQuestionIdAndUserId(createdQuestion.getQuestionId(), prepTextDuelGame.getInvitedUserId())). willReturn(null);
      given(questionService.searchQuestionByQuestionId(createdQuestion.getQuestionId())).willReturn(createdQuestion);

      gameControllerService.intermediateResults(createdQuestion.getGameId());

      List<UserResultTupleDTO> intermediateResult = gameControllerService.intermediateResults(prepTextDuelGame.getGameId());

      for (UserResultTupleDTO userResultTupleDTO : intermediateResult) {
        assertEquals(prepTextDuelGame.getGameId(), userResultTupleDTO.getGameId());
        assertEquals(invitingUser.getId(), userResultTupleDTO.getInvitingPlayerId());
        assertEquals(500L, userResultTupleDTO.getInvitingPlayerResult());
        assertEquals(prepTextDuelGame.getInvitedUserId(), correctAnswer.getUserId());
        assertEquals(0L, userResultTupleDTO.getInvitedPlayerResult());
      }
      assertNotNull(gameControllerService.searchGame(prepTextDuelGame.getGameId()));
    }

    @Test
    void intermediateResults_success() {

        Answer invitingAnswer = new Answer();
        invitingAnswer.setUserId(invitingUser.getId());
        invitingAnswer.setQuestionId(createdQuestion.getQuestionId());
        invitingAnswer.setAnswerString(createdQuestion.getIncorrectAnswers().get(1));
        invitingAnswer.setAnsweredTime(10000L);

        Answer invitedAnswer = new Answer();
        invitedAnswer.setUserId(invitedUser.getId());
        invitedAnswer.setQuestionId(createdQuestion.getQuestionId());
        invitedAnswer.setAnswerString(createdQuestion.getCorrectAnswer());
        invitedAnswer.setAnsweredTime(8000L);

        given(gameService.searchGameById(prepTextDuelGame.getGameId())).willReturn(prepTextDuelGame);
        given(questionService.searchQuestionsByGameId(prepTextDuelGame.getGameId())).willReturn(Arrays.asList(createdQuestion));
        given(answerService.searchAnswerByQuestionIdAndUserId(createdQuestion.getQuestionId(), prepTextDuelGame.getInvitingUserId())). willReturn(invitingAnswer);
        given(answerService.searchAnswerByQuestionIdAndUserId(createdQuestion.getQuestionId(), prepTextDuelGame.getInvitedUserId())). willReturn(invitedAnswer);
        given(questionService.searchQuestionByQuestionId(createdQuestion.getQuestionId())).willReturn(createdQuestion);

        List<UserResultTupleDTO> intermediateResult = gameControllerService.intermediateResults(prepTextDuelGame.getGameId());

        for (UserResultTupleDTO userResultTupleDTO : intermediateResult) {
            assertEquals(prepTextDuelGame.getGameId(), userResultTupleDTO.getGameId());
            assertEquals(invitingUser.getId(), userResultTupleDTO.getInvitingPlayerId());
            assertEquals(0L, userResultTupleDTO.getInvitingPlayerResult());
            assertEquals(prepTextDuelGame.getInvitedUserId(), invitedAnswer.getUserId());
            assertEquals(400000L, userResultTupleDTO.getInvitedPlayerResult());
        }
        assertNotNull(gameControllerService.searchGame(prepTextDuelGame.getGameId()));
    }

    @Test
    void intermediatePoints_timeDifference_fasterGetsMorePoints() {
        Answer fasterAnswer = new Answer();
        fasterAnswer.setUserId(invitingUser.getId());
        fasterAnswer.setQuestionId(createdQuestion.getQuestionId());
        fasterAnswer.setAnswerString(createdQuestion.getCorrectAnswer());
        fasterAnswer.setAnsweredTime(10000L);

        Answer slowerAnswer = new Answer();
        slowerAnswer.setUserId(invitedUser.getId());
        slowerAnswer.setQuestionId(createdQuestion.getQuestionId());
        slowerAnswer.setAnswerString(createdQuestion.getCorrectAnswer());
        slowerAnswer.setAnsweredTime(2000L);

        given(gameService.searchGameById(prepTextDuelGame.getGameId())).willReturn(prepTextDuelGame);
        given(questionService.searchQuestionsByGameId(prepTextDuelGame.getGameId())).willReturn(Arrays.asList(createdQuestion));
        given(answerService.searchAnswerByQuestionIdAndUserId(createdQuestion.getQuestionId(), prepTextDuelGame.getInvitingUserId())). willReturn(fasterAnswer);
        given(answerService.searchAnswerByQuestionIdAndUserId(createdQuestion.getQuestionId(), prepTextDuelGame.getInvitedUserId())). willReturn(slowerAnswer);
        given(questionService.searchQuestionByQuestionId(createdQuestion.getQuestionId())).willReturn(createdQuestion);

        List<UserResultTupleDTO> intermediateResult = gameControllerService.intermediateResults(prepTextDuelGame.getGameId());

        for (UserResultTupleDTO userResultTupleDTO : intermediateResult) {
            assertTrue(userResultTupleDTO.getInvitingPlayerResult() > userResultTupleDTO.getInvitedPlayerResult());
        }
    }

    @Test
    void getEndResult_success() {
      Answer invitingAnswer = new Answer();
      invitingAnswer.setUserId(invitingUser.getId());
      invitingAnswer.setQuestionId(createdQuestion.getQuestionId());
      invitingAnswer.setAnswerString(createdQuestion.getIncorrectAnswers().get(1));
      invitingAnswer.setAnsweredTime(10000L);

      Answer invitedAnswer = new Answer();
      invitedAnswer.setUserId(invitedUser.getId());
      invitedAnswer.setQuestionId(createdQuestion.getQuestionId());
      invitedAnswer.setAnswerString(createdQuestion.getCorrectAnswer());
      invitedAnswer.setAnsweredTime(8000L);

      given(gameService.searchGameById(prepTextDuelGame.getGameId())).willReturn(prepTextDuelGame);
      given(questionService.searchQuestionByQuestionId(createdQuestion.getQuestionId())).willReturn(createdQuestion);
      given(questionService.searchQuestionsByGameId(prepTextDuelGame.getGameId())).willReturn(Arrays.asList(createdQuestion));
      given(answerService.searchAnswerByQuestionIdAndUserId(createdQuestion.getQuestionId(), prepTextDuelGame.getInvitingUserId())).willReturn(invitingAnswer);
      given(answerService.searchAnswerByQuestionIdAndUserId(createdQuestion.getQuestionId(), prepTextDuelGame.getInvitedUserId())).willReturn(invitedAnswer);
      given(gameService.searchGameById(prepTextDuelGame.getGameId())).willReturn(prepTextDuelGame);

      List<UserResultTupleDTO> userResultTupleDTOList = gameControllerService.getEndResult(prepTextDuelGame.getGameId());
      for (UserResultTupleDTO userResultTupleDTO : userResultTupleDTOList) {
        assertEquals(prepTextDuelGame.getGameId(), userResultTupleDTO.getGameId());
        assertEquals(invitingAnswer.getUserId(), userResultTupleDTO.getInvitingPlayerId());
        assertEquals(invitedAnswer.getUserId(), userResultTupleDTO.getInvitedPlayerId());
        assertEquals(0L, userResultTupleDTO.getInvitingPlayerResult());
        assertEquals(400000L, userResultTupleDTO.getInvitedPlayerResult());
      }
    }

  @Test
  void getAllUsersOfGame_success() {
      given(gameService.searchGameById(prepTextDuelGame.getGameId())).willReturn(prepTextDuelGame);

      UserResultTupleDTO userResultTupleDTO = gameControllerService.getAllUsersOfGame(prepTextDuelGame.getGameId());

      assertEquals(prepTextDuelGame.getGameId(), userResultTupleDTO.getGameId());
      assertEquals(prepTextDuelGame.getInvitingUserId(), userResultTupleDTO.getInvitingPlayerId());
      assertEquals(prepTextDuelGame.getInvitedUserId(), userResultTupleDTO.getInvitedPlayerId());
  }

  @Test
  void allUsersConnected_bothConnected_returnsTrue() {
    invitingUser.setStatus(UserStatus.ONLINE);
    invitedUser.setStatus(UserStatus.ONLINE);

    given(gameService.searchGameById(prepTextDuelGame.getGameId())).willReturn(prepTextDuelGame);
    given(userService.searchUserById(invitedUser.getId())).willReturn(invitedUser);
    given(userService.searchUserById(invitingUser.getId())).willReturn(invitingUser);

    Map<String, Boolean> returnedMap = gameControllerService.allUsersConnected(prepTextDuelGame.getGameId());

    assertTrue(returnedMap.get("status"));
  }

  @Test
  void allUsersConnected_oneNotConnected_returnsFalse() {
    invitingUser.setStatus(UserStatus.ONLINE);
    invitedUser.setStatus(UserStatus.OFFLINE);

    given(gameService.searchGameById(prepTextDuelGame.getGameId())).willReturn(prepTextDuelGame);
    given(userService.searchUserById(invitedUser.getId())).willReturn(invitedUser);
    given(userService.searchUserById(invitingUser.getId())).willReturn(invitingUser);

    Map<String, Boolean> returnedMap = gameControllerService.allUsersConnected(prepTextDuelGame.getGameId());

    assertFalse(returnedMap.get("status"));
  }
}