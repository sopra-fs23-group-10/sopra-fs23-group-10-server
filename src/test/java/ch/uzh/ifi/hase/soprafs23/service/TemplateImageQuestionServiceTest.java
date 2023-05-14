package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.TemplateImageQuestion;
import ch.uzh.ifi.hase.soprafs23.repository.TemplateImageQuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

public class TemplateImageQuestionServiceTest {

  @Mock
  private TemplateImageQuestionRepository templateImageQuestionRepository;

  @InjectMocks
  private TemplateImageQuestionService templateImageQuestionService;


  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
  }


  @Test
  void getRandomImageQuestion_success() {

    TemplateImageQuestion oneQuestion = new TemplateImageQuestion();
    oneQuestion.setApiId("622a1c357cc59eab6f94ff56");
    oneQuestion.setCorrectAnswer("Bob Dylan");
    oneQuestion.setIncorrectAnswers(Arrays.asList("Neil Young", "Eric Clapton", "Elton John"));
    oneQuestion.setAllAnswers(Arrays.asList("Neil Young", "Bob Dylan", "Eric Clapton", "Elton John"));
    oneQuestion.setQuestion("Which musician has famously performed over 3,000 shows in their 'Never Ending Tour'?");

    given(templateImageQuestionRepository.count()).willReturn(1L);
    given(templateImageQuestionRepository.findTemplateImageQuestionByTemplateImageQuestionId(2L)).willReturn(oneQuestion);

    TemplateImageQuestion returned = templateImageQuestionService.getRandomImageQuestion();

    assertEquals(oneQuestion.getApiId(), returned.getApiId());
    assertEquals(oneQuestion.getCorrectAnswer(), returned.getCorrectAnswer());
    assertEquals(oneQuestion.getIncorrectAnswers(), returned.getIncorrectAnswers());
    assertEquals(oneQuestion.getAllAnswers(), returned.getAllAnswers());
    assertEquals(oneQuestion.getQuestion(), returned.getQuestion());
  }
}
