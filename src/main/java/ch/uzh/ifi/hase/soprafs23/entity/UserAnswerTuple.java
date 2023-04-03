package ch.uzh.ifi.hase.soprafs23.entity;

public class UserAnswerTuple {
    private long userId;
    private String questionId;
    private String answer;
    private float answeredTime;

    public UserAnswerTuple(long userId, String questionId, String answer, float answeredTime) {
        this.userId = userId;
        this.questionId = questionId;
        this.answer = answer;
        this.answeredTime = answeredTime;
    }

    public UserAnswerTuple() {
    }

    public String toString() {
        return "ResultTuple(userId=" + this.getUserId() + ", answeredTime=" + this.getAnsweredTime() + ")";
    }

    public long getUserId() {
        return this.userId;
    }

    public String getQuestionId() {
        return this.questionId;
    }

    public String getAnswer() {
        return this.answer;
    }

    public float getAnsweredTime() {
        return this.answeredTime;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setAnsweredTime(float answeredTime) {
        this.answeredTime = answeredTime;
    }
}
