package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.Category;
import ch.uzh.ifi.hase.soprafs23.constant.ModeType;
import ch.uzh.ifi.hase.soprafs23.constant.QuizType;
import ch.uzh.ifi.hase.soprafs23.rest.dto.QuestionDTO;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


public class Game {
    private long id;
    private long invitingUserId;
    private long invitedUserId;
    private final Stack<Question> questions = new Stack<>();
    private QuizType quizType;
    private ModeType modeType;

    public Game(long gameId, long invitingUserId, long invitedUserId, QuizType quizType, ModeType modeType) {
        this.id = gameId;
        this.invitingUserId = invitingUserId;
        this.invitedUserId = invitedUserId;
        this.quizType = quizType;
        this.modeType = modeType;
    }

    public Game() {}

    public void addQuestion(Question question){
        questions.add(question);
    }

    public long getId() {
        return this.id;
    }

    public long getInvitingUserId() {
        return this.invitingUserId;
    }

    public long getInvitedUserId() {return this.invitedUserId;}

    public Stack<Question> getQuestions() {
        return this.questions;
    }

    public QuizType getQuizType() {
        return this.quizType;
    }

    public ModeType getModeType() {
        return this.modeType;
    }

    public void setId(long id) {this.id = id;}

    public void setInvitingUserId(long invitingUserId) {
        this.invitingUserId = invitingUserId;
    }

    public void setInvitedUserId(long invitedUserId) {this.invitedUserId = invitedUserId;}

    public void setQuizType(QuizType quizType) {
        this.quizType = quizType;
    }

    public void setModeType(ModeType modeType) {
        this.modeType = modeType;
    }

    public String toString() {
        return "Game(gameId=" + this.getId() + ", invitingUser=" + this.getInvitingUserId() + ", invitedUser=" + this.getInvitedUserId() + ", questions=" + this.getQuestions() + ", quizType=" + this.getQuizType() + ", modeType=" + this.getModeType() + ")";
    }

    public UserResultTuple getResults() {

        UserResultTuple userResultTuple = new UserResultTuple();

        userResultTuple.setInvitedPlayerResult(0L);
        userResultTuple.setInvitedPlayerId(invitedUserId);
        userResultTuple.setInvitingPlayerResult(0L);
        userResultTuple.setInvitingPlayerId(invitingUserId);

        for (Question question : questions) {
            userResultTuple.setInvitedPlayerResult(userResultTuple.getInvitedPlayerResult() + question.getPoints(userResultTuple.getInvitedPlayerId()));
            userResultTuple.setInvitingPlayerResult(userResultTuple.getInvitingPlayerResult() + question.getPoints(userResultTuple.getInvitingPlayerId()));
        }
        return userResultTuple;
    }
}
