package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.Answer;
import ch.uzh.ifi.hase.soprafs23.repository.AnswerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AnswerService {
    private final Logger log = LoggerFactory.getLogger(AnswerService.class);
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
    }
}
