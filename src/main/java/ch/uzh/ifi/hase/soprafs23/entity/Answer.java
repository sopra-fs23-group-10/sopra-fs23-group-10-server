package ch.uzh.ifi.hase.soprafs23.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "ANSWER")
public class Answer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private long id;
    private long questionId;
    private long userId;
    private String answer;
    private long answeredTime;

    public long getId() {
        return this.id;
    }
    public long getQuestionId() {
        return this.questionId;
    }
    public long getUserId() {
        return this.userId;
    }
    public String getAnswer() {
        return this.answer;
    }
    public long getAnsweredTime() {
        return this.answeredTime;
    }

    public void setId(long id) {
        this.id = id;
    }
    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }
    public void setAnswer(String answer) {
        this.answer = answer;
    }
    public void setAnsweredTime(long answeredTime) {
        this.answeredTime = answeredTime;
    }
}
