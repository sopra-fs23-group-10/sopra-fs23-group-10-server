package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.Category;
import ch.uzh.ifi.hase.soprafs23.constant.ModeType;
import ch.uzh.ifi.hase.soprafs23.constant.QuizType;
import ch.uzh.ifi.hase.soprafs23.controller.WebSocketController;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Question;
import ch.uzh.ifi.hase.soprafs23.entity.UserAnswerTuple;
import ch.uzh.ifi.hase.soprafs23.entity.UserResultTuple;
import ch.uzh.ifi.hase.soprafs23.handler.GameMap;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserResultTupleDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

@Service
@Transactional
public class GameService {

    private long index = 0;
    private final GameMap gameMap = GameMap.getInstance();

    @Autowired
    public GameService() {}

    public Question getQuestion(Category category, Long gameId, UserService userService) {

        Game game = gameMap.get(gameId);
        userService.searchUserById(game.getInvitedUserId());
        userService.searchUserById(game.getInvitingUserId());


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
        } catch (InterruptedException e){
            System.err.println("Error sending HTTP request: " + e.getMessage());
            return null;
        } catch(IOException i){
            System.err.println("Error sending HTTP request: " + i.getMessage());
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
                Question createdQuestion = new Question(id, category, correctAnswer, question, incorrectAnswers);
                gameMap.get(gameId).addQuestion(createdQuestion);
                return createdQuestion;
            } catch (JSONException e) {
                System.err.println("Error parsing JSON response: " + e.getMessage());
                return null;
            }
        } else {
            System.err.println("HTTP request failed with status code " + response.statusCode());
            return null;
        }
    }

    public Game createGame(Long invitingUserId, Long invitedUserId, QuizType quizType, ModeType modeType) {
        Game game = new Game(this.index, invitingUserId, invitedUserId, quizType, modeType);
        gameMap.put(game);
        index++;
        return game;
    }

    public Game getGame(Long gameId) {
        return gameMap.get(gameId);
    }

    public void removeGame(Long gameId) {
        gameMap.remove(gameId);
    }

    public Map<String, List<Category>> getRandomTopics(Long gameId, Long requestingUserId) {
        Game game = gameMap.get(gameId);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game does not exist");
        }

        Long currentPlayerId = game.getCurrentPlayer();
        if (!requestingUserId.equals(currentPlayerId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "This user cannot request topics at this point.");
        }

        List<Category> randomTopics = new ArrayList<>(Arrays.asList(Category.values()));
        while (randomTopics.size() > 3) {
            randomTopics.remove((int) ((Math.random() * randomTopics.size())));
        }

        game.changeCurrentPlayer();

        return Collections.singletonMap("topics", randomTopics);
    }

    public Map<String, List<Category>> getAllTopics() {
        return Collections.singletonMap("topics", new ArrayList<>(Arrays.asList(Category.values())));
    }


    public UserResultTuple finishGame(long gameId, UserService userService) {
        Game currentGame = gameMap.get(gameId);
        this.checkGame(currentGame.getId());

        UserResultTuple userResultTuple = currentGame.getResults();
        userService.updatePoints(userResultTuple);
        long invitingUserPoints = userService.getPoints(userResultTuple.getInvitingPlayerId());
        long invitedUserPoints = userService.getPoints(userResultTuple.getInvitedPlayerId());

        UserResultTuple finalResult = new UserResultTuple(gameId,userResultTuple.getInvitingPlayerId(),userResultTuple.getInvitedPlayerId());
        finalResult.setInvitingPlayerResult(invitingUserPoints);
        finalResult.setInvitedPlayerResult(invitedUserPoints);

        return finalResult;
    }

    public UserResultTuple intermediateResults(long gameId) {
        Game currentGame = gameMap.get(gameId);
        this.checkGame(currentGame.getId());
        return currentGame.getResults();
    }

    public Map<Long, Long> answerQuestion(long gameId, UserAnswerTuple userAnswerTuple, UserService userService){

        Game currentGame = gameMap.get(gameId);
        this.checkGame(currentGame.getId());
        currentGame.addAnswer(userAnswerTuple);
        Map scoredPoints = currentGame.getPoints(userAnswerTuple.getUserId());
        if (currentGame.completelyAnswered()) {
            this.finishGame(gameId,userService);
        }
        else {
            currentGame.addAnswer(userAnswerTuple);
        }
        return scoredPoints;
    }

    public void checkGame(Long gameId){
        if (gameMap.get(gameId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game with corresponding gameID cannot be found.");
        }
    }
}
