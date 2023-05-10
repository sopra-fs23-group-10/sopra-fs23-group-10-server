package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.constant.Category;
import ch.uzh.ifi.hase.soprafs23.entity.Question;
import ch.uzh.ifi.hase.soprafs23.entity.TemplateImageQuestion;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.ArrayList;
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

    String correctAnswer = "Dog";
    List<String> answerList = new ArrayList<>();
    answerList.add("Cat");
    answerList.add("Mouse");
    answerList.add("Hamster");

    TemplateImageQuestion templateImageQuestion = new TemplateImageQuestion();
    templateImageQuestion.setTemplateImageQuestionId(2L);
    templateImageQuestion.setApiId("sjpa0Gg");
    templateImageQuestion.setCorrectAnswer(correctAnswer);
    templateImageQuestion.setIncorrectAnswers(answerList);

    answerList.add(correctAnswer);
    templateImageQuestion.setAllAnswers(answerList);

    templateImageQuestion.setQuestion("What kind of animal do you see?");
    entityManager.persist(templateImageQuestion);
    entityManager.flush();
  }
  @Test
  void count_success(){
    long cnt = templateImageQuestionRepository.count();
    assertEquals(1L,cnt);
  }
}