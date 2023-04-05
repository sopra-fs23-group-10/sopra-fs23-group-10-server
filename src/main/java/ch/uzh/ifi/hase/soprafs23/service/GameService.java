package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.Category;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Question;
import ch.uzh.ifi.hase.soprafs23.entity.UserAnswerTuple;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@Transactional
public class GameService {

    private final Logger log = LoggerFactory.getLogger(GameService.class);
    private final UserRepository userRepository;

    @Autowired
    public GameService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Question getQuestion(Category category) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://the-trivia-api.com/api/questions?categories="
                        + category.toString().toLowerCase()
                        + "&limit=1&region=CH&difficulty=easy"
                ))
                .build();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.err.println("Error sending HTTP request: " + e.getMessage());
            return null;
        }
        if (response.statusCode() == 200) {
            try {
                JSONArray jsonArray = new JSONArray(response.body());
                JSONObject jsonObj = jsonArray.getJSONObject(0);
                String id = jsonObj.getString("id");
                String correctAnswer = jsonObj.getString("correctAnswer");
                String[] incorrectAnswers = jsonObj.getJSONArray("incorrectAnswers").toList().toArray(new String[0]);
                String question = jsonObj.getString("question");
                return new Question(id, category, correctAnswer, question, incorrectAnswers);
            } catch (JSONException e) {
                System.err.println("Error parsing JSON response: " + e.getMessage());
                return null;
            }
        } else {
            System.err.println("HTTP request failed with status code " + response.statusCode());
            return null;
        }
    }
}
