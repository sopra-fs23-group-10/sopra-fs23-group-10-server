package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.Answer;
import ch.uzh.ifi.hase.soprafs23.repository.AnswerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class AnswerServiceTest {

  @Mock
  private AnswerRepository answerRepository;

  @InjectMocks
  private AnswerService answerService;

  private Answer prepAnswer;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);

    prepAnswer = new Answer();
    prepAnswer.setId(1L);
    prepAnswer.setQuestionId(5L);
    prepAnswer.setUserId(45L);
    prepAnswer.setAnswerString("someAnswer");
    prepAnswer.setAnsweredTime(15);
  }

  @Test
  void searchAnswerByQuestionIdAndUserId_success() {
    given(answerRepository.findAnswerByQuestionIdAndUserId(prepAnswer.getQuestionId(), prepAnswer.getUserId())).willReturn(prepAnswer);

    Answer found = answerService.searchAnswerByQuestionIdAndUserId(prepAnswer.getQuestionId(), prepAnswer.getUserId());

    assertEquals(prepAnswer.getId(), found.getId());
    assertEquals(prepAnswer.getQuestionId(), found.getQuestionId());
    assertEquals(prepAnswer.getUserId(), found.getUserId());
    assertEquals(prepAnswer.getAnswerString(), found.getAnswerString());
    assertEquals(prepAnswer.getAnsweredTime(), found.getAnsweredTime());
  }


  @Test
  void searchAnswerByQuestionIdAndUserId_noneFound_nullReturned() {
    given(answerRepository.findAnswerByQuestionIdAndUserId(Mockito.any(Long.class), Mockito.any(Long.class))).willReturn(null);

    Answer found = answerService.searchAnswerByQuestionIdAndUserId(prepAnswer.getQuestionId(), prepAnswer.getUserId());

    assertNull(found);
  }

  @Test
  void createAnswer_success() {
    answerService.createAnswer(prepAnswer);
    verify(answerRepository).save(Mockito.any(Answer.class));
  }
}