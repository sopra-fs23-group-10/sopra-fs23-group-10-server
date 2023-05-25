package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.Category;
import ch.uzh.ifi.hase.soprafs23.constant.ModeType;
import ch.uzh.ifi.hase.soprafs23.constant.QuizType;
import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserResultTupleDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.security.SecureRandom;
import java.util.*;

@Service
@Transactional
public class GameControllerService {
    private final UserService userService;
    private final GameService gameService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final TemplateImageQuestionService templateImageQuestionService;
    private final Logger log = LoggerFactory.getLogger(GameControllerService.class);
    private final SecureRandom secureRandom;

    @Autowired
    public GameControllerService(UserService userService, GameService gameService,
                                 QuestionService questionService, AnswerService answerService,
                                 TemplateImageQuestionService templateImageQuestionService) {
        this.userService = userService;
        this.gameService = gameService;
        this.questionService = questionService;
        this.answerService = answerService;
        this.templateImageQuestionService = templateImageQuestionService;
        this.secureRandom = new SecureRandom();
    }

    protected Question getQuestionFromExternalApi(Category category, Long gameId){

        Game game = gameService.searchGameById(gameId);
        userService.searchUserById(game.getInvitedUserId());
        userService.searchUserById(game.getInvitingUserId());

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://the-trivia-api.com/api/questions?categories="
                        + category.toString().toLowerCase()
                        + "&limit=1"
                ))
                .build();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e){
            log.error("InterruptedException during request of external API: {}", e.getMessage());
            Thread.currentThread().interrupt();
            return null;
        } catch (IOException e){
            log.error("IOException during request of external API: {}", e.getMessage());
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
                log.error("Error parsing JSON response of external API: {}", e.getMessage());
                return null;
            }
        } else {
            log.error("Request to external API failed with code: {}", response.statusCode());
            return null;
        }
    }

    protected Question getTemplateQuestion(Category category, Long gameId){
      return questionService.createQuestion(
              gameId,
              "62433573cfaae40c129614a9",
              category,
              "Buzz Aldrin",
              "Who was the second person walking on the Moon?",
              List.of("Neil Armstrong", "Vladimir Komarov", "Yuri Gagarin")
      );
    }

    public Question getQuestion(Category category, Long gameId) {
      Question question;
      try{
        question = this.getQuestionFromExternalApi(category, gameId);
        for (int i = 0; i < 6; i++) {
          if (question != null && !questionService.existsQuestionByApiIdAndGameId(question)) {
            break;
          }
          question = this.getQuestionFromExternalApi(category, gameId);
        }
        if (question == null){
          question = getTemplateQuestion(category, gameId);
        }
        return questionService.saveQuestion(question);
      } catch (Exception e){
        question = getTemplateQuestion(category, gameId);
      }
      return questionService.saveQuestion(question);
    }

  private Question getImageQuestionFromApi(Long gameId){
    Game game = gameService.searchGameById(gameId);
    userService.searchUserById(game.getInvitedUserId());
    userService.searchUserById(game.getInvitingUserId());
    try{
      TemplateImageQuestion templateImageQuestion = templateImageQuestionService.getRandomImageQuestion();
      if (templateImageQuestion == null){return null;}
      return questionService.createImageQuestion(game.getGameId(), templateImageQuestion.getApiId(),
              templateImageQuestion.getCorrectAnswer(), templateImageQuestion.getQuestion(),
              templateImageQuestion.getIncorrectAnswers(), templateImageQuestion.getAllAnswers());
    }catch (ResponseStatusException e){
      log.error("Question not found");
      return null;
    }
  }

  public Question getImageQuestion(Long gameId) {
    Question question = this.getImageQuestionFromApi(gameId);
    for (int i = 0; i < 7; i++) {
      if (question != null && !questionService.existsQuestionByApiIdAndGameId(question)) {
        break;
      }
      question = this.getImageQuestionFromApi(gameId);
    }

    if (question == null){
      return questionService.createImageQuestion(
              gameId,
              "sjpa0Gg",
              "Dog",
              "What kind of animal do you see?",
              Arrays.asList("Cat,Mouse,Hamster".split(",")),
              Arrays.asList("Cat,Mouse,Hamster,Dog".split(","))
      );
    }
    return questionService.saveQuestion(question);
  }

    public Game searchGame(Long gameId) {
        return gameService.searchGameById(gameId);
    }

    public Game createGame(Long invitingUserId, Long invitedUserId, QuizType quizType, ModeType modeType) {
        userService.setInGame(invitingUserId);
        if (!invitedUserId.equals(0L)) {
            userService.setInGame(invitedUserId);
        }
        return gameService.createGame(invitingUserId, invitedUserId, quizType, modeType);
    }

    public void setInGamePlayersToOnline(Long gameId) {
        Game game = gameService.searchGameById(gameId);
        User invitingUser = userService.searchUserById(game.getInvitingUserId());
        if (invitingUser != null && invitingUser.getStatus() == UserStatus.IN_GAME) {
            userService.setOnline(game.getInvitingUserId());
        }
        User invitedUser = userService.searchUserById(game.getInvitedUserId());
        if (invitedUser != null && invitedUser.getStatus() == UserStatus.IN_GAME) {
            userService.setOnline(game.getInvitedUserId());
        }
    }

    public Map<String, List<Category>> getRandomTopics(Long gameId) {
        List<Category> randomTopics = new ArrayList<>(Arrays.asList(Category.values()));
        while (randomTopics.size() > 3) {
            randomTopics.remove(secureRandom.nextInt(randomTopics.size()));
        }

        gameService.changeCurrentPlayer(gameId);

        return Collections.singletonMap("topics", randomTopics);
    }

    public Map<String, List<Category>> getAllTopics() {
        return Collections.singletonMap("topics", new ArrayList<>(Arrays.asList(Category.values())));
    }

    public synchronized String answerQuestion(Answer answer) {
        if (answer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "The received answer cannot be null.");
        }

        Answer foundAnswer = answerService.searchAnswerByQuestionIdAndUserId(answer.getQuestionId(), answer.getUserId());
        if (foundAnswer != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User has already answered this question.");
        }

        Question question = questionService.searchQuestionByQuestionId(answer.getQuestionId());

        if (question.timeRunUp()) {
            answer.setAnswerString("WrongAnswer");
        }
        answerService.createAnswer(answer);
        return question.getCorrectAnswer();
    }

    private UserResultTuple getPointsOfBoth(Game game) {
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

      if (questions != null && !questions.isEmpty()) {
        questions.sort((q1, q2) -> Long.compare(q1.getQuestionId(), q2.getQuestionId()));
      }

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

    public List<UserResultTupleDTO> getEndResult(long gameId) {
        List<UserResultTupleDTO> userResultTupleDTOList = this.intermediateResults(gameId);

        UserResultTuple userResultTuple = getPointsOfBoth(gameService.searchGameById(gameId));
        userService.updatePoints(userResultTuple);

        UserResultTupleDTO userResultTupleDTO = DTOMapper.INSTANCE.convertUserResultTupleEntitytoDTO(userResultTuple);
        userResultTupleDTOList.add(userResultTupleDTO);
        return userResultTupleDTOList;
    }

    public UserResultTupleDTO getAllUsersOfGame(Long gameId){
        Game game = gameService.searchGameById(gameId);
        long invitingUserId = game.getInvitingUserId();
        long invitedUserId = game.getInvitedUserId();
        UserResultTuple userResultTuple = new UserResultTuple(gameId, invitingUserId, invitedUserId);
        return DTOMapper.INSTANCE.convertUserResultTupleEntitytoDTO(userResultTuple);
    }

    public Map<String, Boolean> allUsersConnected(long gameId) {
        Game game = gameService.searchGameById(gameId);
        return Collections.singletonMap(
                "status",
                userService.searchUserById(game.getInvitingUserId()).getStatus().equals(UserStatus.ONLINE)
                        && userService.searchUserById(game.getInvitedUserId()).getStatus().equals(UserStatus.ONLINE));
    }

    public long getPoints(Answer answer) {
        if (answer == null) {
          return 0L;
        }
        Question question = questionService.searchQuestionByQuestionId(answer.getQuestionId());
        return answer.getAnswerString().equals(question.getCorrectAnswer()) ?
                (long) (15L + (double)answer.getAnsweredTime()) : 0L;
    }

    public Boolean bothAnswered(long gameId, long questionId){
      Game game = gameService.searchGameById(gameId);
      return answerService.searchAnswerByQuestionIdAndUserId(questionId, game.getInvitingUserId()) != null
              && answerService.searchAnswerByQuestionIdAndUserId(questionId, game.getInvitedUserId()) != null;
    }
}
