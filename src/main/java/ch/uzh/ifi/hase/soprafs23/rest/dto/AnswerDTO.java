package ch.uzh.ifi.hase.soprafs23.rest.dto;

public class AnswerDTO {

    private Long userId;
    private Long questionId;
    private String answerString;
    private Long answeredTime;

    public Long getUserId() {
        return this.userId;
    }

    public Long getQuestionId() {
        return this.questionId;
    }

    public String getAnswer() {
        return this.answerString;
    }

    public Long getAnsweredTime() {
        return this.answeredTime;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public void setAnswer(String answer) {
        this.answerString = answer;
    }

    public void setAnsweredTime(Long answeredTime) {
        this.answeredTime = answeredTime;
    }
}
