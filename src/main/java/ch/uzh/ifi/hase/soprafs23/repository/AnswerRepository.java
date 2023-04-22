package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("answerRepository")
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Answer findAnswerById(long answerId);
    List<Answer> findAnswersByQuestionId(long questionId);
    Answer findAnswerByQuestionIdAndUserId(long questionId, long userId);
    List<Answer> deleteAllByQuestionId(Long questionId);
}
