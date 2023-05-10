package ch.uzh.ifi.hase.soprafs23.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class TemplateImageQuestionRepositoryTest {
  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private TemplateImageQuestionRepository templateImageQuestionRepository;

  @Test
  void count_success(){
    long cnt = templateImageQuestionRepository.count();
    assertEquals(4, cnt);
  }
}