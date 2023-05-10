package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.Category;
import ch.uzh.ifi.hase.soprafs23.entity.Question;
import ch.uzh.ifi.hase.soprafs23.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class QuestionService {
    private final QuestionRepository questionRepository;

    @Autowired
    public QuestionService(@Qualifier("questionRepository") QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public Question searchQuestionByQuestionId(long questionId) {
        Question question = questionRepository.findQuestionByQuestionId(questionId);
        if (question == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Question cannot be found.");
        }
        return question;
    }

    public Boolean existsQuestionByApiIdAndGameId(Question question) {
        return questionRepository.existsQuestionByApiIdAndGameId(question.getApiId(), question.getGameId());
    }

    public List<Question> searchQuestionsByGameId(long gameId) {
        return questionRepository.findAllByGameId(gameId);
    }

    public Question createQuestion(long gameId, String apiId, Category category, String correctAnswer, String questionString, List<String> incorrectAnswers) {
        Question question = new Question();
        question.setGameId(gameId);
        question.setQuestion(questionString);
        question.setCategory(category);
        question.setApiId(apiId);
        question.setCorrectAnswer(correctAnswer);
        question.setIncorrectAnswers(incorrectAnswers);
        List<String> allAnswers = new ArrayList<>(List.copyOf(incorrectAnswers));
        allAnswers.add(correctAnswer);
        Collections.shuffle(allAnswers);
        question.setAllAnswers(allAnswers);
        question.setCreationTime(new Date());
        return question;
    }

  public Question createImageQuestion(long templateImageQuestionId, String apiId, String correctAnswer, String questionString, List<String> incorrectAnswers,List<String> allAnswers) {
    Question question = new Question();
    question.setGameId(templateImageQuestionId);
    question.setQuestion(questionString);
    question.setApiId(apiId);
    question.setCorrectAnswer(correctAnswer);
    question.setIncorrectAnswers(incorrectAnswers);

    Collections.shuffle(allAnswers);
    question.setAllAnswers(allAnswers);
    question.setCreationTime(new Date());
    System.out.println("Question "+correctAnswer+" created");
    return question;
  }

    public Question saveQuestion(Question question){
      System.out.println("Question "+question.getCorrectAnswer()+" stored");
      return questionRepository.save(question);
    }
}
