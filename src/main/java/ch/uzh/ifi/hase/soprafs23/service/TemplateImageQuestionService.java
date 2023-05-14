package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.TemplateImageQuestion;
import ch.uzh.ifi.hase.soprafs23.repository.TemplateImageQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@Transactional
public class TemplateImageQuestionService {

  private final TemplateImageQuestionRepository templateImageQuestionRepository;
  private final SecureRandom secureRandom = new SecureRandom();
  @Autowired
  public TemplateImageQuestionService(@Qualifier("templateImageQuestionRepository") TemplateImageQuestionRepository templateImageQuestionRepository) {
    this.templateImageQuestionRepository = templateImageQuestionRepository;
  }

  public TemplateImageQuestion getRandomImageQuestion() {
    long qty = templateImageQuestionRepository.count();
    int id = getRandomInt((int)qty+1);

    return templateImageQuestionRepository.findTemplateImageQuestionByTemplateImageQuestionId(id);
  }

  private int getRandomInt(int max){
    int min = 2;
    return min + secureRandom.nextInt(max - min + 1);
  }
}
