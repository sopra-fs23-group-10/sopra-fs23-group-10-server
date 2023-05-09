package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.converter.StringListConverter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "IMAGE_QUESTION")
public class TemplateImageQuestion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long templateImageQuestionId;
    @Column(nullable = true)
    private String apiId;
    @Column(nullable = false)
    private String correctAnswer;
    @Column(nullable = false)
    @Convert(converter = StringListConverter.class)
    private List<String> incorrectAnswers;
    @Column(nullable = false)
    @Convert(converter = StringListConverter.class)
    private List<String> allAnswers;
    @Column(nullable = false)
    private String question;

    public long getTemplateImageQuestionId() {
        return this.templateImageQuestionId;
    }
    public String getApiId() {
        return this.apiId;
    }
    public String getCorrectAnswer() {
        return this.correctAnswer;
    }
    public List<String> getIncorrectAnswers() {
        return this.incorrectAnswers;
    }
    public List<String> getAllAnswers() {
        return this.allAnswers;
    }
    public String getQuestion() {
        return this.question;
    }

    public void setTemplateImageQuestionId(long templateImageQuestionId) {
        this.templateImageQuestionId = templateImageQuestionId;
    }
    public void setApiId(String apiId) {
        this.apiId = apiId;
    }
    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
    public void setIncorrectAnswers(List<String> incorrectAnswers) {
        this.incorrectAnswers = incorrectAnswers;
    }
    public void setAllAnswers(List<String> allAnswers) {
        this.allAnswers = allAnswers;
    }
    public void setQuestion(String question) {
        this.question = question;
    }
}
