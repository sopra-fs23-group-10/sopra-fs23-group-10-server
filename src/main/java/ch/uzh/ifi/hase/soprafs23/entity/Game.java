package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.Category;
import ch.uzh.ifi.hase.soprafs23.constant.ModeType;
import ch.uzh.ifi.hase.soprafs23.constant.QuizType;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Stack;

public class Game {
    private long gameId;
    private long invitingUserId;
    private long invitedUserId;
    private final Stack<Question> questions = new Stack<>();
    private QuizType quizType;
    private ModeType modeType;

    public Game(long gameId, long invitingUserId, long invitedUserId, QuizType quizType, ModeType modeType){
        this.gameId = gameId;
        this.invitingUserId = invitingUserId;
        this.invitedUserId = invitedUserId;
        this.quizType = quizType;
        this.modeType = modeType;
    }

    public Game() {
    }

    public void getQuestion(Category category) throws IOException, InterruptedException, JSONException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://the-trivia-api.com/api/questions?categories="
                        + category.toString().toLowerCase()
                        + "&limit=1&region=CH&difficulty=easy"))
                .build();
        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONObject obj = new JSONObject(response.body());
            System.out.println(obj);
        }
    }

    public long getGameId() {
        return this.gameId;
    }

    public long getInvitingUserId() {
        return this.invitingUserId;
    }

    public long getInvitedUserId() {
        return this.invitedUserId;
    }

    public Stack<Question> getQuestions() {
        return this.questions;
    }

    public QuizType getQuizType() {
        return this.quizType;
    }

    public ModeType getModeType() {
        return this.modeType;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public void setInvitingUserId(long invitingUserId) {
        this.invitingUserId = invitingUserId;
    }

    public void setInvitedUserId(long invitedUserId) {
        this.invitedUserId = invitedUserId;
    }

    public void setQuizType(QuizType quizType) {
        this.quizType = quizType;
    }

    public void setModeType(ModeType modeType) {
        this.modeType = modeType;
    }

    public String toString() {
        return "Game(gameId=" + this.getGameId() + ", invitingUser=" + this.getInvitingUserId() + ", invitedUser=" + this.getInvitedUserId() + ", questions=" + this.getQuestions() + ", quizType=" + this.getQuizType() + ", modeType=" + this.getModeType() + ")";
    }
}
