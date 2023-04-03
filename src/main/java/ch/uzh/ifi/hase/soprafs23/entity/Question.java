package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.Category;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;

public class Question {
    private Category category;
    private String id;
    private String correctAnswer;
    private String[] incorrectAnswers;
    private String[] allAnswers;
    private String question;
    private HashMap<Long, UserAnswerTuple> results = new HashMap<>();

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

    public HashMap<Long, UserAnswerTuple> getResults() {
        return this.results;
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

    public void setResults(HashMap<Long, UserAnswerTuple> results) {
        this.results = results;
    }

    public long getPoints(long userId) {
        long addedScore = 20L;
        /*for (UserAnswerTuple userAnswerTuple : results) {
            if (userAnswerTuple.getUserId() == userId) {
                addedScore += (long) userAnswerTuple.getPoints();
            }
        }*/
        return addedScore;
    }

    public void addAnswer(UserAnswerTuple userAnswerTuple) {
        try {
            results.put(userAnswerTuple.getUserId(), userAnswerTuple);
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User has already answered this question.");
        }
    }
}
