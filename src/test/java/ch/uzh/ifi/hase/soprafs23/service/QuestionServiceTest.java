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
    createdQuestion.setQuestion("Which musician has famously performed over 3,000 shows in their 'Never Ending Tour'?");
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
    assertEquals(createdQuestion.getQuestion(), found.getQuestion());
    assertEquals(createdQuestion.getCreationTime(), found.getCreationTime());
  }
}