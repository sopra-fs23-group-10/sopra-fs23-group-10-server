package ch.uzh.ifi.hase.soprafs23.rest.dto;

public class QuestionResultDTO {

    private long gameId;
    private String questionId;
    private String answer;

    public long getGameId() {
        return this.gameId;
    }

    public String getQuestionId() {
        return this.questionId;
    }

    public String getAnswer() {
        return this.answer;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
