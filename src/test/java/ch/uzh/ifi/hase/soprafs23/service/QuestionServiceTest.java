package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.Category;
import ch.uzh.ifi.hase.soprafs23.constant.ModeType;
import ch.uzh.ifi.hase.soprafs23.constant.QuizType;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Question;
import ch.uzh.ifi.hase.soprafs23.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

class QuestionServiceTest {

  @Mock
  private QuestionRepository questionRepository;

  @InjectMocks
  private QuestionService questionService;

  private Question createdQuestion;
  private Game prepGame;

  private final List<String> incorrectAnswers = Arrays.asList("Neil Young", "Eric Clapton", "Elton John");
  private final List<String> allAnswers = Arrays.asList("Neil Young", "Bob Dylan", "Eric Clapton", "Elton John");

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);

    prepGame = new Game();
    prepGame.setGameId(1L);
    prepGame.setInvitingUserId(3L);
    prepGame.setInvitedUserId(4L);
    prepGame.setQuizType(QuizType.TEXT);
    prepGame.setModeType(ModeType.DUEL);
    prepGame.setCurrentPlayer(4L);

    createdQuestion = new Question();
    createdQuestion.setGameId(prepGame.getGameId());
    createdQuestion.setQuestionId(2L);
    createdQuestion.setCategory(Category.MUSIC);
    createdQuestion.setApiId("622a1c357cc59eab6f94ff56");
    createdQuestion.setCorrectAnswer("Bob Dylan");
    createdQuestion.setIncorrectAnswers(incorrectAnswers);
    createdQuestion.setAllAnswers(allAnswers);
    createdQuestion.setQuestionString("Which musician has famously performed over 3,000 shows in their 'Never Ending Tour'?");
    createdQuestion.setCreationTime(new Date());
  }

  @Test
  void searchQuestionByQuestionId_success() {
    given(questionRepository.findQuestionByQuestionId(createdQuestion.getQuestionId())).willReturn(createdQuestion);

    Question found = questionService.searchQuestionByQuestionId(createdQuestion.getQuestionId());

    assertEquals(createdQuestion.getGameId(), found.getGameId());
    assertEquals(createdQuestion.getQuestionId(), found.getQuestionId());
    assertEquals(createdQuestion.getCategory(), found.getCategory());
    assertEquals(createdQuestion.getApiId(), found.getApiId());
    assertEquals(createdQuestion.getCorrectAnswer(), found.getCorrectAnswer());
    assertEquals(createdQuestion.getIncorrectAnswers(), found.getIncorrectAnswers());
    assertEquals(createdQuestion.getAllAnswers(), found.getAllAnswers());
    assertEquals(createdQuestion.getQuestionString(), found.getQuestionString());
    assertEquals(createdQuestion.getCreationTime(), found.getCreationTime());
  }

  @Test
  void existsQuestionByApiIdAndGameId_success() {
    given(questionRepository.existsQuestionByApiIdAndGameId(createdQuestion.getApiId(), prepGame.getGameId())).willReturn(true);
    assertTrue(questionService.existsQuestionByApiIdAndGameId(createdQuestion));
  }

  @Test
  void searchQuestionsByGameId_success() {
    given(questionRepository.findAllByGameId(prepGame.getGameId())).willReturn(Arrays.asList(createdQuestion));

    List<Question> returnedQuestions = questionService.searchQuestionsByGameId(prepGame.getGameId());

    assertEquals(1, returnedQuestions.size());
    for (Question question : returnedQuestions) {
      assertEquals(createdQuestion.getGameId(), question.getGameId());
      assertEquals(createdQuestion.getQuestionId(), question.getQuestionId());
      assertEquals(createdQuestion.getCategory(), question.getCategory());
      assertEquals(createdQuestion.getApiId(), question.getApiId());
      assertEquals(createdQuestion.getCorrectAnswer(), question.getCorrectAnswer());
      assertEquals(createdQuestion.getIncorrectAnswers(), question.getIncorrectAnswers());
      assertEquals(createdQuestion.getAllAnswers(), question.getAllAnswers());
      assertEquals(createdQuestion.getQuestionString(), question.getQuestionString());
      assertEquals(createdQuestion.getCreationTime(), question.getCreationTime());
    }
  }

  @Test
  void createQuestion_success() {
    Question returnedQuestion = questionService.createQuestion(createdQuestion.getGameId(),createdQuestion.getApiId(), createdQuestion.getCategory(), createdQuestion.getCorrectAnswer(), createdQuestion.getQuestionString(), createdQuestion.getIncorrectAnswers());

    assertEquals(createdQuestion.getGameId(), returnedQuestion.getGameId());
    assertEquals(createdQuestion.getCategory(), returnedQuestion.getCategory());
    assertEquals(createdQuestion.getApiId(), returnedQuestion.getApiId());
    assertEquals(createdQuestion.getCorrectAnswer(), returnedQuestion.getCorrectAnswer());
    assertEquals(createdQuestion.getIncorrectAnswers(), returnedQuestion.getIncorrectAnswers());
    assertEquals(createdQuestion.getAllAnswers().size(), returnedQuestion.getAllAnswers().size());
    assertTrue(returnedQuestion.getAllAnswers().containsAll(createdQuestion.getAllAnswers()));
    assertEquals(createdQuestion.getQuestionString(), returnedQuestion.getQuestionString());
    assertEquals(createdQuestion.getCreationTime().toString(), returnedQuestion.getCreationTime().toString());
  }

  @Test
  void createImageQuestion_success() {
    Question imageQuestion = new Question();
    imageQuestion.setQuestionId(123L);
    imageQuestion.setApiId("apiIdForSomeStupidImageQuestion");
    imageQuestion.setCorrectAnswer("dark");
    imageQuestion.setIncorrectAnswers(Arrays.asList("bright", "money", "sexy"));
    imageQuestion.setAllAnswers(Arrays.asList("frog", "money", "dark", "sexy"));
    imageQuestion.setQuestionString("What does your future look like?");

    Question createdQuestion = questionService.createImageQuestion(prepGame.getGameId(), imageQuestion.getApiId(), imageQuestion.getCorrectAnswer(), imageQuestion.getQuestionString(), imageQuestion.getIncorrectAnswers(), imageQuestion.getAllAnswers());

    assertEquals(prepGame.getGameId(), createdQuestion.getGameId());
    assertEquals(imageQuestion.getQuestionString(), createdQuestion.getQuestionString());
    assertEquals(imageQuestion.getApiId(), createdQuestion.getApiId());
    assertEquals(imageQuestion.getCorrectAnswer(), createdQuestion.getCorrectAnswer());
    assertEquals(imageQuestion.getIncorrectAnswers().size(), createdQuestion.getIncorrectAnswers().size());
    assertTrue(createdQuestion.getIncorrectAnswers().containsAll(imageQuestion.getIncorrectAnswers()));
    assertEquals(imageQuestion.getAllAnswers().size(), createdQuestion.getAllAnswers().size());
    assertTrue(createdQuestion.getAllAnswers().containsAll(imageQuestion.getAllAnswers()));
    assertNotNull(createdQuestion.getCreationTime());
  }
}