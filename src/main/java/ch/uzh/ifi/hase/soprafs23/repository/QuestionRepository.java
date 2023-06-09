package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("questionRepository")
public interface QuestionRepository extends JpaRepository<Question, Long> {

    Question findQuestionByQuestionId(long questionId);
    Boolean existsQuestionByApiIdAndGameId(String apiId, Long gameId);
    List<Question> findAllByGameId(long gameId);
}
