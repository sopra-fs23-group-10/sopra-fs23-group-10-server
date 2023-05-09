package ch.uzh.ifi.hase.soprafs23.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "ANSWER")
public class Answer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private long questionId;
    @Column(nullable = false)
    private long userId;
    @Column(nullable = false)
    private String answer;
    @Column(nullable = false)
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
