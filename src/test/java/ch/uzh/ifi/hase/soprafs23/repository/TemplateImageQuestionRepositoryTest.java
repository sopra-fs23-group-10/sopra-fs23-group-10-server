package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.TemplateImageQuestion;
import org.junit.jupiter.api.Test;
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

  @Test
  void count_success(){
    long cnt = templateImageQuestionRepository.count();
    assertEquals(4L,cnt);
  }
}