package ch.uzh.ifi.hase.soprafs23.service;


import ch.uzh.ifi.hase.soprafs23.entity.Answer;
import ch.uzh.ifi.hase.soprafs23.repository.AnswerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@SpringBootTest
public class AnswerServiceIntegrationTest {

  @Qualifier("answerRepository")
  @Autowired
  private AnswerRepository answerRepository;

  @Autowired
  private AnswerService answerService;

  private Answer prepAnswer;


  @BeforeEach
  public void setup() {
    answerRepository.deleteAll();

    prepAnswer = new Answer();
    prepAnswer.setQuestionId(5L);
    prepAnswer.setUserId(45L);
    prepAnswer.setAnswerString("someAnswer");
    prepAnswer.setAnsweredTime(15);
  }


  @Test
  void searchAnswerByQuestionIdAndUserId_success() {
    Answer savedAnswer = answerRepository.save(prepAnswer);
    Answer foundAnswer = answerService.searchAnswerByQuestionIdAndUserId(savedAnswer.getQuestionId(), savedAnswer.getUserId());

    assertEquals(savedAnswer.getId(), foundAnswer.getId());
    assertEquals(savedAnswer.getQuestionId(), foundAnswer.getQuestionId());
    assertEquals(savedAnswer.getUserId(), foundAnswer.getUserId());
    assertEquals(savedAnswer.getAnswerString(), foundAnswer.getAnswerString());
    assertEquals(savedAnswer.getAnsweredTime(), foundAnswer.getAnsweredTime());
  }


  @Test
  void searchAnswerByQuestionIdAndUserId_noneFound_null() {
    Answer foundAnswer = answerService.searchAnswerByQuestionIdAndUserId(prepAnswer.getQuestionId(), prepAnswer.getUserId());
    assertNull(foundAnswer);
  }

  @Test
  void createAnswer_success() {
    answerService.createAnswer(prepAnswer);
    Answer foundAnswer = answerRepository.findAnswerByQuestionIdAndUserId(prepAnswer.getQuestionId(), prepAnswer.getUserId());

    assertEquals(prepAnswer.getQuestionId(), foundAnswer.getQuestionId());
    assertEquals(prepAnswer.getUserId(), foundAnswer.getUserId());
    assertEquals(prepAnswer.getAnswerString(), foundAnswer.getAnswerString());
    assertEquals(prepAnswer.getAnsweredTime(), foundAnswer.getAnsweredTime());
  }
}
