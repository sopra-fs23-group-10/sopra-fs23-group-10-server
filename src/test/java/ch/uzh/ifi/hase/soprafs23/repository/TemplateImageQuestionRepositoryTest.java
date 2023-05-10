package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.constant.Category;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Question;
import ch.uzh.ifi.hase.soprafs23.entity.TemplateImageQuestion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TemplateImageQuestionRepositoryTest {
  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private TemplateImageQuestionRepository templateImageQuestionRepository;

  private TemplateImageQuestion templateImageQuestion;

  @BeforeEach
  private void setup() {

    // set up valid question
    this.templateImageQuestion = new TemplateImageQuestion();
    List<String> incorrectAnswers =
            java.util.Arrays.asList(
                    "Cat",
                    "Mouse",
                    "Hamster"
            );
    List<String> allAnswers =
            java.util.Arrays.asList(
                    "Dog",
                    "Cat",
                    "Mouse",
                    "Hamster"
            );
    templateImageQuestion.setApiId("sjpa0Gg");
    templateImageQuestion.setCorrectAnswer("Dog");
    templateImageQuestion.setIncorrectAnswers(incorrectAnswers);
    templateImageQuestion.setAllAnswers(allAnswers);
    templateImageQuestion.setQuestion("What kind of animal do you see?");

    // store first question in DB
    entityManager.persist(templateImageQuestion);
    entityManager.flush();
  }

  @Test
  void findGameByInvitingUserId_success() {
    TemplateImageQuestion found = templateImageQuestionRepository.findTemplateImageQuestionBytemplateImageQuestionId(templateImageQuestion.getTemplateImageQuestionId());

    assertNotNull(found);
    assertEquals(found.getTemplateImageQuestionId(), templateImageQuestion.getTemplateImageQuestionId());
    assertEquals(found.getQuestion(), templateImageQuestion.getQuestion());
    assertEquals(found.getApiId(), templateImageQuestion.getApiId());
    assertEquals(found.getAllAnswers(), templateImageQuestion.getAllAnswers());
    assertEquals(found.getCorrectAnswer(), templateImageQuestion.getCorrectAnswer());
    assertEquals(found.getIncorrectAnswers(), templateImageQuestion.getIncorrectAnswers());
  }

  @Test
  void findQuestionByQuestionId_noneFound(){
    TemplateImageQuestion found = templateImageQuestionRepository.findTemplateImageQuestionBytemplateImageQuestionId(-1L);
    assertNull(found);
  }
}