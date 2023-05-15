package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.Answer;
import ch.uzh.ifi.hase.soprafs23.repository.AnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AnswerService {
    private final AnswerRepository answerRepository;

    @Autowired
    public AnswerService(@Qualifier("answerRepository") AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }

    public Answer searchAnswerByQuestionIdAndUserId(long questionId, long userId) {
        return answerRepository.findAnswerByQuestionIdAndUserId(questionId, userId);
    }

    public void createAnswer(Answer answer) {
        Answer createdAnswer = new Answer();
        createdAnswer.setQuestionId(answer.getQuestionId());
        createdAnswer.setUserId(answer.getUserId());
        createdAnswer.setAnswerString(answer.getAnswerString());
        createdAnswer.setAnsweredTime(answer.getAnsweredTime());

        answerRepository.save(createdAnswer);
    }
}
