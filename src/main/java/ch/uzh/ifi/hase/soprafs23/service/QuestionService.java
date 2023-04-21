package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.Answer;
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


    public void setResults(Map<Long, UserAnswerTuple> results) {
        this.results = results;
    }

    public long getPoints(long questionId, long userId) {
        Answer answer = AnswerService.searchAnswerByUserId(questionId, userId);
        UserAnswerTuple userAnswerTuple = results.get(userId);

        if (userAnswerTuple == null) {
            return 0L;
        }

        return userAnswerTuple.getAnswer().equals(this.correctAnswer) ?
                (long) (500L - (0.5 * userAnswerTuple.getAnsweredTime())) : 0L;
    }

    public synchronized void addAnswer(Answer answer) {
        Answer foundAnswer = answerService.searchAnswerByQuestionIdAndUserId(answer.getQuestionId(), answer.getUserId());
        if (foundAnswer != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User has already answered this question.");
        } else {
            answerService.createAnswer(answer);
        }
    }

    public Boolean completelyAnswered(){
        return results.size() >= 2;
    }
}
