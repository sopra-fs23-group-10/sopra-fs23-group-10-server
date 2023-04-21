package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.Category;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "QUESTION")
public class Question implements Serializable {


    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private long questionId;
    private long gameId;
    private Category category;
    private String apiId;
    private String correctAnswer;
    private String incorrectAnswers;
    private String allAnswers;
    private String questionString;

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
    public String getIncorrectAnswers() {
        return this.incorrectAnswers;
    }
    public String getAllAnswers() {
        return this.allAnswers;
    }
    public String getQuestionString() {
        return this.questionString;
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
    public void setIncorrectAnswers(String incorrectAnswers) {
        this.incorrectAnswers = incorrectAnswers;
    }
    public void setAllAnswers(String allAnswers) {
        this.allAnswers = allAnswers;
    }
    public void setQuestionString(String questionString) {
        this.questionString = questionString;
    }
}
