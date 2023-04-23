package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.Category;
import ch.uzh.ifi.hase.soprafs23.constant.ModeType;
import ch.uzh.ifi.hase.soprafs23.constant.QuizType;
import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Answer;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Question;
import ch.uzh.ifi.hase.soprafs23.entity.UserResultTuple;
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
public class GameControllerService {
    private final UserService userService;
    private final GameService gameService;
    private final QuestionService questionService;
    private final AnswerService answerService;

    @Autowired
    public GameControllerService(UserService userService, GameService gameService, QuestionService questionService, AnswerService answerService) {
        this.userService = userService;
        this.gameService = gameService;
        this.questionService = questionService;
        this.answerService = answerService;
    }

    public Question getQuestion(Category category, Long gameId) {

        Game game = gameService.searchGameById(gameId);
        userService.searchUserById(game.getInvitedUserId());
        userService.searchUserById(game.getInvitingUserId());


        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://the-trivia-api.com/api/questions?categories="
                        + category.toString().toLowerCase()
                        + "&limit=1&difficulty=easy"
                ))
                .build();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e){
            System.err.println("Error during request of external API: " + e.getMessage());
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
                return questionService.createQuestion(game.getGameId(), id, category, correctAnswer, question, List.of(incorrectAnswers));
            } catch (JSONException e) {
                System.err.println("Error parsing JSON response of external API: " + e.getMessage());
                return null;
            }
        } else {
            System.err.println("Request to external API failed with code: " + response.statusCode());
            return null;
        }
    }

    public Game searchGame(Long gameId) {
        return gameService.searchGameById(gameId);
    }

    public Game createGame(Long invitingUserId, Long invitedUserId, QuizType quizType, ModeType modeType) {
        return gameService.createGame(invitingUserId, invitedUserId, quizType, modeType);
    }

    public void removeGame(Long gameId) {
        Game game = gameService.searchGameById(gameId);
        userService.setOnline(game.getInvitedUserId());
        userService.setOnline(game.getInvitingUserId());
        gameService.deleteGame(gameId);
    }

    public Map<String, List<Category>> getRandomTopics(Long gameId, Long requestingUserId) {
        //TODO: Does this possibly throw a NullPointerException?
        if (!requestingUserId.equals(gameService.searchGameById(gameId).getCurrentPlayer())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "This user cannot request topics at this point.");
        }

        List<Category> randomTopics = new ArrayList<>(Arrays.asList(Category.values()));
        while (randomTopics.size() > 3) {
            randomTopics.remove((int) ((Math.random() * randomTopics.size())));
        }

        gameService.changeCurrentPlayer(gameId);

        return Collections.singletonMap("topics", randomTopics);
    }

    public Map<String, List<Category>> getAllTopics() {
        return Collections.singletonMap("topics", new ArrayList<>(Arrays.asList(Category.values())));
    }

    public synchronized void answerQuestion(long gameId, Answer answer) {
        if (answer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "The received answer cannot be null.");
        }

        Answer foundAnswer = answerService.searchAnswerByQuestionIdAndUserId(answer.getQuestionId(), answer.getUserId());
        if (foundAnswer != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User has already answered this question.");
        }

        questionService.searchQuestionByQuestionId(answer.getQuestionId());

        if (gameService.timeRunUp(gameId)) {

            Answer placeholderAnswer = new Answer();
            placeholderAnswer.setUserId(answer.getUserId());
            placeholderAnswer.setQuestionId(answer.getQuestionId());
            placeholderAnswer.setAnswer("WrongAnswerAnywayBecauseYouTookTooLong");
            placeholderAnswer.setAnsweredTime(1000L);

            answerService.createAnswer(placeholderAnswer);
        }
        else {
            answerService.createAnswer(answer);
        }

    }

    public UserResultTuple getPointsOfBoth(Game game) {
        UserResultTuple userResultTuple = new UserResultTuple(game.getGameId(), game.getInvitingUserId(), game.getInvitedUserId());
        List<Question> questions = questionService.searchQuestionsByGameId(game.getGameId());
        for (Question question : questions) {
            Answer invitingAnswer = answerService.searchAnswerByQuestionIdAndUserId(question.getQuestionId(), game.getInvitingUserId());
            userResultTuple.setInvitingPlayerResult(userResultTuple.getInvitingPlayerResult() + this.getPoints(invitingAnswer));
            Answer invitedAnswer = answerService.searchAnswerByQuestionIdAndUserId(question.getQuestionId(), game.getInvitedUserId());
            userResultTuple.setInvitedPlayerResult(userResultTuple.getInvitedPlayerResult() + this.getPoints(invitedAnswer));
        }
        return userResultTuple;
    }

    public List<UserResultTupleDTO> intermediateResults(long gameId) {
        Game game = gameService.searchGameById(gameId);

        List<UserResultTupleDTO> userResultTupleDTOList= new ArrayList<>();

        List<Question> questions = questionService.searchQuestionsByGameId(gameId);

        for (Question question : questions) {
            UserResultTuple userResultTuple = new UserResultTuple(game.getGameId(), game.getInvitingUserId(), game.getInvitedUserId());

            Answer invitingAnswer = answerService.searchAnswerByQuestionIdAndUserId(question.getQuestionId(), game.getInvitingUserId());
            userResultTuple.setInvitingPlayerResult(this.getPoints(invitingAnswer));
            Answer invitedAnswer = answerService.searchAnswerByQuestionIdAndUserId(question.getQuestionId(), game.getInvitedUserId());
            userResultTuple.setInvitedPlayerResult(this.getPoints(invitedAnswer));

            UserResultTupleDTO userResultTupleDTO = DTOMapper.INSTANCE.convertUserResultTupleEntitytoDTO(userResultTuple);
            userResultTupleDTOList.add(userResultTupleDTO);
        }
        return userResultTupleDTOList;
    }

    public List<UserResultTupleDTO> finishGame(long gameId) {
        List<UserResultTupleDTO> userResultTupleDTOList = this.intermediateResults(gameId);

        UserResultTuple userResultTuple = getPointsOfBoth(gameService.searchGameById(gameId));
        userService.updatePoints(userResultTuple);

        UserResultTupleDTO userResultTupleDTO = DTOMapper.INSTANCE.convertUserResultTupleEntitytoDTO(userResultTuple);
        userResultTupleDTOList.add(userResultTupleDTO);
        this.removeGame(gameId);
        return userResultTupleDTOList;
    }

    public UserResultTupleDTO getAllUsersOfGame(Long gameId){
        Game game = gameService.searchGameById(gameId);
        UserResultTuple userResultTuple = new UserResultTuple(gameId, game.getInvitingUserId(), game.getInvitedUserId());
        return DTOMapper.INSTANCE.convertUserResultTupleEntitytoDTO(userResultTuple);
    }

    public Map<String, Boolean> allUsersConnected(long gameId) {
        Game game = gameService.searchGameById(gameId);
        return Collections.singletonMap(
                "status",
                userService.searchUserById(game.getInvitingUserId()).getStatus().equals(UserStatus.ONLINE)
                        && userService.searchUserById(game.getInvitedUserId()).getStatus().equals(UserStatus.ONLINE));
    }

    public long getGameIdOfUser(Long userId){
        return gameService.getGameIdOfUser(userId);
    }

    public long getPoints(Answer answer) {
        if (answer == null) {
            return 0L;
        }

        Question question = questionService.searchQuestionByQuestionId(answer.getQuestionId());
        return answer.getAnswer().equals(question.getCorrectAnswer()) ?
                (long) (750L - (0.5 * answer.getAnsweredTime()/10)) : 0L;
    }

    public synchronized boolean completelyAnswered(long gameId) {
        List<Question> questions = questionService.searchQuestionsByGameId(gameId);
        for (Question question : questions) {
            if (!answerService.completelyAnswered(question.getQuestionId())) {
                return false;
            }
        }
        return true;
    }
}
