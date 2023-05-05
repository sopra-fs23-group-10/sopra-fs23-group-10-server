package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.Question;
import ch.uzh.ifi.hase.soprafs23.entity.TemplateImageQuestion;
import ch.uzh.ifi.hase.soprafs23.repository.TemplateImageQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
    int idx = secureRandom.nextInt((int)qty);
    Page<TemplateImageQuestion> questionPage = templateImageQuestionRepository.findAll(PageRequest.of(idx, 1));

    if (questionPage.hasContent()) {
      return questionPage.getContent().get(0);
    }else {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Question not found");
    }
  }
}
