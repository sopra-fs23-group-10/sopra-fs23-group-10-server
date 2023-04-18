package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.Category;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Question {
    private Category category;
    private String id;
    private String correctAnswer;
    private String[] incorrectAnswers;
    private String[] allAnswers;
    private String questionString;
    private Map<Long, UserAnswerTuple> results = new HashMap<>();

    public Question(String id, Category category, String correctAnswer, String question, String[] incorrectAnswers) {
        this.category = category;
        this.id = id;
        this.questionString = question;
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
        return this.questionString;
    }

    public Map<Long, UserAnswerTuple> getResults() {
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
        this.questionString = question;
    }

    public void setResults(Map<Long, UserAnswerTuple> results) {
        this.results = results;
    }

    public long getPoints(long userId) {
        UserAnswerTuple userAnswerTuple = results.get(userId);

        if (userAnswerTuple == null) {
            return 0L;
        }

        return userAnswerTuple.getAnswer().equals(this.correctAnswer) ?
                (long) (500L - (0.5 * userAnswerTuple.getAnsweredTime())) : 0L;
    }

    public Map<String, Boolean> lastCorrect(long userId) {
        UserAnswerTuple userAnswerTuple = results.get(userId);
        if (userAnswerTuple != null) {
            return Collections.singletonMap("boolean", userAnswerTuple.getAnswer().equals(this.correctAnswer));
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User has not answered this question yet.");
        }
    }

    public void addAnswer(UserAnswerTuple userAnswerTuple) {
        if (results.get(userAnswerTuple.getUserId()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User has already answered this question.");
        } else {
            results.put(userAnswerTuple.getUserId(), userAnswerTuple);
        }
    }

    public Boolean completelyAnswered(){
        return results.size() >= 2;
    }
}
