package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.Category;

import java.util.ArrayList;

public class Question {
    private long gameId;
    private Category category;
    private String id;
    private String correctAnswer;
    private String[] incorrectAnswers;
    private String[] allAnswers;
    private String question;
    private ArrayList<ResultTuple> results = new ArrayList<>();

    public Question(String id, Category category, String correctAnswer, String question, String[] incorrectAnswers) {
        this.category = category;
        this.id = id;
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.incorrectAnswers = incorrectAnswers;

        this.allAnswers = new String[incorrectAnswers.length + 1];
        this.allAnswers[0] = correctAnswer;
        System.arraycopy(incorrectAnswers, 0, this.allAnswers, 1, incorrectAnswers.length);
    }

    public Question() {
    }

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

    public String[] getAllAnswers() {
        return this.allAnswers;
    }

    public String getQuestion() {
        return this.question;
    }

    public ArrayList<ResultTuple> getResults() {
        return this.results;
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

    public void setAllAnswers(String[] allAnswers) {
        this.allAnswers = allAnswers;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setResults(ArrayList<ResultTuple> results) {
        this.results = results;
    }
}
