package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.TemplateImageQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("templateImageQuestionRepository")
public interface TemplateImageQuestionRepository extends JpaRepository<TemplateImageQuestion, Long> {
}
