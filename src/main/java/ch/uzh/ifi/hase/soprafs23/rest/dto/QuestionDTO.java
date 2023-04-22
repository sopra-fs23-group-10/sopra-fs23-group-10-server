package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.constant.Category;

import java.util.List;

public class QuestionDTO {
    private long gameId;
    private long questionId;
    private Category category;
    private String apiId;
    private String correctAnswer;
    private List<String> incorrectAnswers;
    private List<String> allAnswers;
    private String question;

    public long getGameId() {
        return this.gameId;
    }
    public long getQuestionId() {
        return this.questionId;
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

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }
    public void setQuestionId(long questionId) {
        this.questionId = questionId;
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
    public void setQuestion(String question) {
        this.question = question;
    }
}
