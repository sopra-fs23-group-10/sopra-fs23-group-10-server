package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.Category;
import ch.uzh.ifi.hase.soprafs23.constant.ModeType;
import ch.uzh.ifi.hase.soprafs23.constant.QuizType;
import lombok.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Stack;

@Getter
@Setter
@ToString
public class Game {
    private final long gameId;
    private final ArrayList<User> users;
    private final Stack<Question> questions = new Stack<>();
    private final QuizType quizType;
    private final ModeType modeType;

    public Game(long gameId, ArrayList<User> users, Stack<Question> questions, QuizType quizType, ModeType modeType){
        this.gameId = gameId;
        this.users = users;
        this.quizType = quizType;
        this.modeType = modeType;
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
}
