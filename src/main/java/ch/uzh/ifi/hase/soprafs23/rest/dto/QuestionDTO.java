package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.constant.Category;

public class QuestionDTO {
    private long gameId;
    private Category category;
    private String id;
    private String correctAnswer;
    private String[] incorrectAnswers;
    private String question;

    public long getGameId() {
        return this.gameId;
    }

    public Category getCategory() {
        return this.category;
    }

    public String getId() {
        return this.id;
    }

    public String getCorrectAnswer() {
        return this.correctAnswer;
    }

    public String[] getIncorrectAnswers() {
        return this.incorrectAnswers;
    }

    public String getQuestion() {
        return this.question;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public void setIncorrectAnswers(String[] incorrectAnswers) {
        this.incorrectAnswers = incorrectAnswers;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
