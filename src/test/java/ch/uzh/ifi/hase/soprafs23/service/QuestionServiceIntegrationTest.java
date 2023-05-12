package ch.uzh.ifi.hase.soprafs23.service;


import ch.uzh.ifi.hase.soprafs23.constant.Category;
import ch.uzh.ifi.hase.soprafs23.entity.Question;
import ch.uzh.ifi.hase.soprafs23.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@WebAppConfiguration
@SpringBootTest
public class QuestionServiceIntegrationTest {
  @Qualifier("questionRepository")
  @Autowired
  private QuestionRepository questionRepository;

  @Autowired
  private QuestionService questionService;

  private Question prepQuestion;

  private final List<String> incorrectAnswers = Arrays.asList("Neil Young", "Eric Clapton", "Elton John");
  private final List<String> allAnswers = Arrays.asList("Neil Young", "Bob Dylan", "Eric Clapton", "Elton John");

  @BeforeEach
  public void setup() {
    questionRepository.deleteAll();

    prepQuestion = new Question();
    prepQuestion.setGameId(11L);
    prepQuestion.setCategory(Category.MUSIC);
    prepQuestion.setApiId("622a1c357cc59eab6f94ff56");
    prepQuestion.setCorrectAnswer("Bob Dylan");
    prepQuestion.setIncorrectAnswers(incorrectAnswers);
    prepQuestion.setAllAnswers(allAnswers);
    prepQuestion.setQuestion("Which musician has famously performed over 3,000 shows in their 'Never Ending Tour'?");
    prepQuestion.setCreationTime(new Date());
  }


  @Test
  void searchQuestionByQuestionId_success() {
    Question savedQuestion = questionRepository.save(prepQuestion);

    Question foundQuestion = questionService.searchQuestionByQuestionId(savedQuestion.getQuestionId());

    assertEquals(savedQuestion.getGameId(), foundQuestion.getGameId());
    assertEquals(savedQuestion.getQuestionId(), foundQuestion.getQuestionId());
    assertEquals(savedQuestion.getCategory(), foundQuestion.getCategory());
    assertEquals(savedQuestion.getApiId(), foundQuestion.getApiId());
    assertEquals(savedQuestion.getCorrectAnswer(), foundQuestion.getCorrectAnswer());
    assertEquals(savedQuestion.getIncorrectAnswers(), foundQuestion.getIncorrectAnswers());
    assertEquals(savedQuestion.getAllAnswers(), foundQuestion.getAllAnswers());
    assertEquals(savedQuestion.getQuestion(), foundQuestion.getQuestion());
    assertEquals(savedQuestion.getCreationTime(), foundQuestion.getCreationTime());
  }

  @Test
  void searchQuestionByQuestionId_noneFound_throwsException() {
    assertThrows(ResponseStatusException.class,() -> questionService.searchQuestionByQuestionId(1648L));
  }
}
