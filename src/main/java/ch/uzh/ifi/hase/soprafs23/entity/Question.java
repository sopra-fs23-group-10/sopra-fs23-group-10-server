package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.Category;
import ch.uzh.ifi.hase.soprafs23.converter.StringListConverter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "QUESTION")
public class Question implements Serializable {


    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private long questionId;
    @Column(nullable = false)
    private long gameId;
    @Column(nullable = false)
    private Category category;
    @Column(nullable = false)
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

    public long getQuestionId() {
        return this.questionId;
    }
    public long getGameId() {
        return this.gameId;
    }
    public Category getCategory() {
        return this.category;
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

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }
    public void setGameId(long gameId) {
        this.gameId = gameId;
    }
    public void setCategory(Category category) {
        this.category = category;
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
    public void setQuestion(String questionString) {
        this.question = questionString;
    }
}
