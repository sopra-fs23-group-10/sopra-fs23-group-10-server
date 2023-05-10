package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("answerRepository")
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Answer findAnswerByQuestionIdAndUserId(long questionId, long userId);
}
