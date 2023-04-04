package ch.uzh.ifi.hase.soprafs23.rest.dto;

public class UserAnswerDTO {

    private Long userId;
    private String questionId;
    private String answer;
    private Long answeredTime;

    public Long getUserId() {
        return this.userId;
    }

    public String getQuestionId() {
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

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setAnsweredTime(Long answeredTime) {
        this.answeredTime = answeredTime;
    }
}
