package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.Category;
import ch.uzh.ifi.hase.soprafs23.entity.Question;
import ch.uzh.ifi.hase.soprafs23.repository.QuestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class QuestionService {
    private final Logger log = LoggerFactory.getLogger(QuestionService.class);
    private final QuestionRepository questionRepository;
    private final AnswerService answerService;

    @Autowired
    public QuestionService(@Qualifier("questionRepository") QuestionRepository questionRepository, AnswerService answerService) {
        this.questionRepository = questionRepository;
        this.answerService = answerService;
    }

    public Question searchQuestionByQuestionId(long questionId) {
        Question question = questionRepository.findQuestionByQuestionId(questionId);
        if (question == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Question cannot be found.");
        }
        return question;
    }

    public List<Question> searchQuestionsByGameId(long gameId) {
        /*
        if (questionList.size() == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game does not have any questions yet");
        }*/
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

        return questionRepository.save(question);
    }

    public List<Question> deleteQuestions(Long gameId) {
        return questionRepository.deleteAllByGameId(gameId);
    }
}
