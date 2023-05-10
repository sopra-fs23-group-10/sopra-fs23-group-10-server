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

  @Autowired
  public TemplateImageQuestionService(@Qualifier("templateImageQuestionRepository") TemplateImageQuestionRepository templateImageQuestionRepository) {
    this.templateImageQuestionRepository = templateImageQuestionRepository;
  }


  public TemplateImageQuestion getRandomImageQuestion() {
    final SecureRandom secureRandom = new SecureRandom();

    long qty = templateImageQuestionRepository.count();
    int idx = secureRandom.nextInt(2,(int) qty+2);
    return templateImageQuestionRepository.findTemplateImageQuestionByTemplateImageQuestionId(idx);
  }
}
