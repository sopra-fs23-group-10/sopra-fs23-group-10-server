package ch.uzh.ifi.hase.soprafs23.rest.dto;

public class AnswerDTO {

    private Long userId;
    private Long questionId;
    private String answer;
    private Long answeredTime;

    public Long getUserId() {
        return this.userId;
    }

    public Long getQuestionId() {
        return this.questionId;
    }

    public String getAnswer() {
        return this.answer;
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
        this.answer = answer;
    }

    public void setAnsweredTime(Long answeredTime) {
        this.answeredTime = answeredTime;
    }
}
