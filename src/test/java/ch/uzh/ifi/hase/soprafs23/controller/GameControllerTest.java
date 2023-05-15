package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.Category;
import ch.uzh.ifi.hase.soprafs23.constant.ModeType;
import ch.uzh.ifi.hase.soprafs23.constant.QuizType;
import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.rest.dto.AnswerDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GameDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.QuestionDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserResultTupleDTO;
import ch.uzh.ifi.hase.soprafs23.service.GameControllerService;
import ch.uzh.ifi.hase.soprafs23.service.QuestionService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import ch.uzh.ifi.hase.soprafs23.service.WebSocketService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.*;

import static java.lang.String.format;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameController.class)
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private GameControllerService gameControllerService;

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    @MockBean
    private WebSocketController webSocketController;

    @MockBean
    private WebSocketService webSocketService;

    @Autowired
    private GameController gameController;

    @MockBean
    private QuestionService questionService;

    private User invitingUser;
    private Question question;
    private Question imageQuestion;
    private Game game;

    @BeforeEach
    void setup() {
        // set up valid user
        invitingUser = new User();
        invitingUser.setId(1L);
        invitingUser.setToken("inviting");
        invitingUser.setStatus(UserStatus.ONLINE);

        // set up valid game
        game = new Game();
        game.setGameId(0L);
        game.setInvitingUserId(invitingUser.getId());
        game.setInvitedUserId(2L);
        game.setQuizType(QuizType.TEXT);
        game.setModeType(ModeType.DUEL);

        // set up valid question
        question = new Question();
        question.setQuestionId(69L);
        question.setApiId("62433573cfaae40c129614a9");
        question.setQuestionString("Who wrote this test?");
        question.setCategory(Category.GENERAL_KNOWLEDGE);
        String[] answers = {"me", "you", "allOfUs", "WhoKnows?"};
        question.setAllAnswers(List.of(answers));
        String[] incorrectAnswers = {"you", "allOfUs", "WhoKnows?"};
        question.setIncorrectAnswers(List.of(incorrectAnswers));
        question.setCorrectAnswer("me");
        question.setCreationTime(new Date());



      // set up valid imageQuestion
      imageQuestion = new Question();
      imageQuestion.setQuestionId(70L);
      imageQuestion.setApiId("6243Aet3vQ");
      imageQuestion.setQuestionString("Who wrote this test?");
      String[] imageAnswers = {"me", "you", "allOfUs", "WhoKnows?"};
      imageQuestion.setAllAnswers(List.of(imageAnswers));
      String[] incorrectImageAnswers = {"you", "allOfUs", "WhoKnows?"};
      imageQuestion.setIncorrectAnswers(List.of(incorrectImageAnswers));
      imageQuestion.setCorrectAnswer("me");
      imageQuestion.setCreationTime(new Date());
    }

    @Test
    void createGame_thenGameReturned_201() throws Exception {
        User invitedUser = new User();
        invitedUser.setId(game.getInvitedUserId());
        invitedUser.setToken("invited");
        invitedUser.setStatus(UserStatus.ONLINE);

        GameDTO gameDTO = new GameDTO();
        gameDTO.setInvitingUserId(game.getInvitingUserId());
        gameDTO.setInvitedUserId(game.getInvitedUserId());
        gameDTO.setQuizType(QuizType.TEXT);
        gameDTO.setModeType(ModeType.DUEL);

        given(userService.verifyToken(invitingUser.getToken())).willReturn(invitingUser);
        given(userService.searchUserById(gameDTO.getInvitedUserId())).willReturn(invitedUser);
        given(userService.searchUserById(gameDTO.getInvitingUserId())).willReturn(invitingUser);
        given(gameControllerService.createGame(invitingUser.getId(), invitedUser.getId(), gameDTO.getQuizType(), gameDTO.getModeType())).willReturn(game);

        MockHttpServletRequestBuilder postRequest = post("/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gameDTO))
                .header("token", invitingUser.getToken());

        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.gameId", is((int) game.getGameId())))
                .andExpect(jsonPath("$.invitedUserId", is((int) game.getInvitedUserId())))
                .andExpect(jsonPath("$.invitingUserId", is((int) game.getInvitingUserId())))
                .andExpect(jsonPath("$.modeType", is(game.getModeType().toString())))
                .andExpect(jsonPath("$.quizType", is(game.getQuizType().toString())));

        verify(webSocketController).inviteUser(eq(invitedUser.getId()), Mockito.any(GameDTO.class));
    }

    @Test
    void createGame_invalidToken_throwsUnauthorized_401() throws Exception {
        User invitedUser = new User();
        invitedUser.setId(game.getInvitedUserId());

        GameDTO gameDTO = new GameDTO();
        gameDTO.setInvitingUserId(game.getInvitingUserId());
        gameDTO.setInvitedUserId(game.getInvitedUserId());
        gameDTO.setQuizType(QuizType.TEXT);
        gameDTO.setModeType(ModeType.DUEL);

        given(userService.verifyToken(invitingUser.getToken())).willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Provided token is invalid."));
        given(userService.searchUserById(gameDTO.getInvitedUserId())).willReturn(invitedUser);
        given(userService.searchUserById(gameDTO.getInvitingUserId())).willReturn(invitingUser);
        given(gameControllerService.createGame(invitingUser.getId(), invitedUser.getId(), gameDTO.getQuizType(), gameDTO.getModeType())).willReturn(game);

        MockHttpServletRequestBuilder postRequest = post("/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gameDTO))
                .header("token", invitingUser.getToken());

        mockMvc.perform(postRequest).andExpect(status().isUnauthorized());
    }

  @Test
  void respondInvitation_trueResponse_200() throws Exception {
    User invitedUser = new User();
    invitedUser.setId(45L);
    invitedUser.setToken("invitingToken");
    game.setInvitedUserId(invitedUser.getId());

    given(userService.verifyToken(invitedUser.getToken())).willReturn(invitedUser);
    given(gameControllerService.searchGame(game.getGameId())).willReturn(game);

    MockHttpServletRequestBuilder postRequest = post("/games/" + game.getGameId() + "/invitation")
            .contentType(MediaType.APPLICATION_JSON)
            .content(String.valueOf(true))
            .header("token", invitedUser.getToken());

    mockMvc.perform(postRequest)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$." + game.getGameId(), is(true)));

    verify(webSocketController).sendInvitationRespond(eq(game.getInvitedUserId()), any());
    verify(webSocketController).sendInvitationRespond(eq(game.getInvitingUserId()), any());
  }

  @Test
  void respondInvitation_falseResponse_200() throws Exception {
    User invitedUser = new User();
    invitedUser.setId(45L);
    invitedUser.setToken("invitingToken");
    game.setInvitedUserId(invitedUser.getId());

    given(userService.verifyToken(invitedUser.getToken())).willReturn(invitedUser);
    given(gameControllerService.searchGame(game.getGameId())).willReturn(game);

    MockHttpServletRequestBuilder postRequest = post("/games/" + game.getGameId() + "/invitation")
            .contentType(MediaType.APPLICATION_JSON)
            .content(String.valueOf(false))
            .header("token", invitedUser.getToken());

    mockMvc.perform(postRequest)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$." + game.getGameId(), is(false)));

    verify(gameControllerService).setInGamePlayersToOnline(game.getGameId());
  }

    @Test
    void createQuestion_thenQuestionCreated_201() throws Exception {
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setCategory(question.getCategory());
        questionDTO.setGameId(game.getGameId());

        given(userService.verifyToken(Mockito.any())).willReturn(invitingUser);
        given(gameControllerService.getQuestion(questionDTO.getCategory(), questionDTO.getGameId())).willReturn(question);


        MockHttpServletRequestBuilder postRequest = post("/games/topics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(questionDTO))
                .header("token", "helloToken");

        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.gameId", is((int) game.getGameId())))
                .andExpect(jsonPath("$.category", is(question.getCategory().toString())))
                .andExpect(jsonPath("$.questionId", is((int) question.getQuestionId())))
                .andExpect(jsonPath("$.correctAnswer").value(nullValue()))
                .andExpect(jsonPath("$.incorrectAnswers").value(nullValue()))
                .andExpect(jsonPath("$.allAnswers", containsInAnyOrder("me", "you", "allOfUs", "WhoKnows?")))
                .andExpect(jsonPath("$.question", is(question.getQuestionString())));
    }

    @Test
    void createQuestion_invalidToken_throwsUnauthorized_401() throws Exception {
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setCategory(question.getCategory());
        questionDTO.setGameId(game.getGameId());

        given(userService.verifyToken(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Provided token is invalid."));
        given(gameControllerService.getQuestion(questionDTO.getCategory(), questionDTO.getGameId())).willReturn(question);


        MockHttpServletRequestBuilder postRequest = post("/games/topics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(questionDTO))
                .header("token", "helloToken");

        mockMvc.perform(postRequest).andExpect(status().isUnauthorized());
    }

    @Test
    void getTopicSelection_whenValid_thenReturnTopics_200() throws Exception {
        List<Category> categories = new ArrayList<>();
        categories.add(Category.MUSIC);
        categories.add(Category.FILM_TV);
        categories.add(Category.GENERAL_KNOWLEDGE);

        given(userService.verifyToken(invitingUser.getToken())).willReturn(invitingUser);
        given(gameControllerService.getRandomTopics(0L)).willReturn(Collections.singletonMap("topics", categories));

        MockHttpServletRequestBuilder getRequest = get("/games/" + game.getGameId() + "/topics")
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", invitingUser.getToken());

        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.topics[0]", is(categories.get(0).toString())))
                .andExpect(jsonPath("$.topics[1]", is(categories.get(1).toString())))
                .andExpect(jsonPath("$.topics[2]", is(categories.get(2).toString())));
    }

    @Test
    void getTopicSelection_whenInvalidToken_thenThrowUnauthorized_401() throws Exception {
        List<Category> categories = new ArrayList<>();
        categories.add(Category.MUSIC);
        categories.add(Category.FILM_TV);
        categories.add(Category.GENERAL_KNOWLEDGE);

        String invalidToken = "someInvalidToken";

        when(userService.verifyToken(invalidToken)).thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Provided token is invalid."));
        given(userService.searchUserById(invitingUser.getId())).willReturn(invitingUser);

        MockHttpServletRequestBuilder getRequest = get("/games/" + game.getGameId() + "/topics")
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", invalidToken);

        mockMvc.perform(getRequest).andExpect(status().isUnauthorized());
    }

    @Test
    void getAllTopics_whenValid_thenSuccess_200() throws Exception {
        Map<String, List<Category>> topics = Collections.singletonMap("topics", new ArrayList<>(Arrays.asList(Category.values())));

        given(userService.verifyToken(invitingUser.getToken())).willReturn(invitingUser);
        given(gameControllerService.getAllTopics()).willReturn(topics);

        MockHttpServletRequestBuilder getRequest = get("/games/topics/all")
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", invitingUser.getToken());

        mockMvc.perform(getRequest).andExpect(status().isOk());
    }

    @Test
    void answerQuestion_whenQuestionNotAnswered_thenSuccess_201() throws Exception {
        AnswerDTO answerDTO = new AnswerDTO();
        answerDTO.setUserId(invitingUser.getId());
        answerDTO.setQuestionId(question.getQuestionId());
        answerDTO.setAnswerString(question.getCorrectAnswer());
        answerDTO.setAnsweredTime(112L);

        given(userService.verifyToken(invitingUser.getToken())).willReturn(invitingUser);
        given(gameControllerService.answerQuestion(Mockito.any())).willReturn(question.getCorrectAnswer());

        MockHttpServletRequestBuilder putRequest = put("/games/" + game.getGameId() + "/question")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(answerDTO))
                .header("token", invitingUser.getToken());

        mockMvc.perform(putRequest).andExpect(status().isCreated())
                .andExpect(jsonPath("$.correctAnswer", is(question.getCorrectAnswer())));
    }

    @Test
    void answerQuestion_whenNullAnswer_thenNotAcceptable_406() throws Exception {
        AnswerDTO answerDTO = new AnswerDTO();

        given(userService.verifyToken(Mockito.any())).willReturn(invitingUser);
        Mockito.doThrow(new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "The received answer cannot be null.")).when(gameControllerService).answerQuestion(Mockito.any(Answer.class));


        MockHttpServletRequestBuilder putRequest = put("/games/" + game.getGameId() + "/question")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(answerDTO))
                .header("token", invitingUser.getToken());

        mockMvc.perform(putRequest).andExpect(status().isNotAcceptable());
    }

    @Test
    void answerQuestion_alreadyAnswered_thenConflict_409() throws Exception {
        AnswerDTO answerDTO = new AnswerDTO();
        answerDTO.setUserId(invitingUser.getId());
        answerDTO.setQuestionId(question.getQuestionId());
        answerDTO.setAnswerString(question.getAllAnswers().get(2));
        answerDTO.setAnsweredTime(112L);

        given(userService.verifyToken(Mockito.any())).willReturn(invitingUser);
        Mockito.doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "User has already answered this question.")).when(gameControllerService).answerQuestion(Mockito.any(Answer.class));

        MockHttpServletRequestBuilder putRequest = put("/games/" + game.getGameId() + "/question")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(answerDTO))
                .header("token", invitingUser.getToken());

        mockMvc.perform(putRequest).andExpect(status().isConflict());
    }

    @Test
    void allUsersConnected_bothConnected_validInput_success() throws Exception {
        given(userService.verifyToken(invitingUser.getToken())).willReturn(invitingUser);
        given(gameControllerService.allUsersConnected(game.getGameId())).willReturn(Collections.singletonMap("status", true));

        MockHttpServletRequestBuilder getRequest = get("/games/" + game.getGameId() + "/online")
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", invitingUser.getToken());

        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(true)));
    }

    @Test
    void allUsersConnected_oneNotConnected_validInput_success() throws Exception {
        given(userService.verifyToken(invitingUser.getToken())).willReturn(invitingUser);
        given(gameControllerService.allUsersConnected(game.getGameId())).willReturn(Collections.singletonMap("status", false));

        MockHttpServletRequestBuilder getRequest = get("/games/" + game.getGameId() + "/online")
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", invitingUser.getToken());

        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(false)));
    }

    @Test
    void allUsersConnected_bothConnected_invalidToken_throws_401() throws Exception {
        given(userService.verifyToken(invitingUser.getToken())).willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Provided token is invalid."));
        given(gameControllerService.allUsersConnected(game.getGameId())).willReturn(Collections.singletonMap("status", true));

        MockHttpServletRequestBuilder getRequest = get("/games/" + game.getGameId() + "/online")
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", invitingUser.getToken());

        mockMvc.perform(getRequest).andExpect(status().isUnauthorized());
    }

    @Test
    void finishGame_whenPointsUpdated_thenUserResultTupleDTO_200() throws Exception {
        // set user to status IN_GAME
        invitingUser.setStatus(UserStatus.IN_GAME);

        // set up result tuple with points assigned to both users
        UserResultTuple userResultTuple = new UserResultTuple(
                game.getGameId(),
                invitingUser.getId(),
                game.getInvitedUserId()
        );
        userResultTuple.setInvitingPlayerResult(100L);
        userResultTuple.setInvitedPlayerResult(200L);

        // create DTO of result tuple
        UserResultTupleDTO userResultTupleDTO = new UserResultTupleDTO();
        userResultTupleDTO.setGameId(userResultTuple.getGameId());
        userResultTupleDTO.setInvitingPlayerId(
                userResultTuple.getInvitingPlayerId());
        userResultTupleDTO.setInvitingPlayerResult(
                userResultTuple.getInvitingPlayerResult());
        userResultTupleDTO.setInvitedPlayerId(
                userResultTuple.getInvitedPlayerId());
        userResultTupleDTO.setInvitedPlayerResult(
                userResultTuple.getInvitedPlayerResult());

        // put DTO of userResult to Array
        List<UserResultTupleDTO> userResultTupleDTOs = new ArrayList<>();
        userResultTupleDTOs.add(userResultTupleDTO);

        // mock token verification
        given(userService.verifyToken(invitingUser.getToken()))
                .willReturn(invitingUser);

        // mock call to service class for finishing game
        given(
                gameControllerService.getEndResult(game.getGameId()))
                .willReturn(userResultTupleDTOs);

        // prepare delete request with url, body and header
        MockHttpServletRequestBuilder getRequest = get("/games/" + game.getGameId() + "/finish")
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", invitingUser.getToken());

        // execute delete request and check if returned DTO matches set up
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].gameId", is((int) game.getGameId())))
                .andExpect(jsonPath("$[0].invitingPlayerId",
                        is((int) invitingUser.getId())))
                .andExpect(jsonPath("$[0].invitingPlayerResult",
                        is((int) userResultTuple.getInvitingPlayerResult())))
                .andExpect(jsonPath("$[0].invitedPlayerId",
                        is((int) game.getInvitedUserId())))
                .andExpect(jsonPath("$[0].invitedPlayerResult",
                        is((int) userResultTuple.getInvitedPlayerResult())));
    }

    @Test
    void finishGame_whenInvalidToken_thenThrowUnautorized_401() throws Exception {
        invitingUser.setStatus(UserStatus.IN_GAME);

        User invitedUser = new User();
        invitedUser.setId(game.getInvitedUserId());

        UserResultTuple userResultTuple = new UserResultTuple(game.getGameId(), invitingUser.getId(), invitedUser.getId());
        userResultTuple.setInvitingPlayerResult(100L);
        userResultTuple.setInvitedPlayerResult(200L);

        UserResultTupleDTO userResultTupleDTO = new UserResultTupleDTO();
        userResultTupleDTO.setGameId(userResultTuple.getGameId());
        userResultTupleDTO.setInvitingPlayerId(userResultTuple.getInvitingPlayerId());
        userResultTupleDTO.setInvitingPlayerResult(userResultTuple.getInvitingPlayerResult());
        userResultTupleDTO.setInvitedPlayerId(userResultTuple.getInvitedPlayerId());
        userResultTupleDTO.setInvitedPlayerResult(userResultTuple.getInvitedPlayerResult());

        List<UserResultTupleDTO> userResultTupleDTOs = new ArrayList<>();
        userResultTupleDTOs.add(userResultTupleDTO);

        given(userService.verifyToken(invitingUser.getToken())).willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Provided token is invalid."));
        given(gameControllerService.getEndResult(game.getGameId())).willReturn(userResultTupleDTOs);

        MockHttpServletRequestBuilder getRequest = get("/games/" + game.getGameId() + "/finish")
                .header("token", invitingUser.getToken());

        mockMvc.perform(getRequest).andExpect(status().isUnauthorized());
    }

    @Test
    void intermediateGame_whenValid_returnResults_200() throws Exception {
        invitingUser.setStatus(UserStatus.IN_GAME);

        UserResultTuple userResultTuple = new UserResultTuple(game.getGameId(), invitingUser.getId(), game.getInvitedUserId());
        userResultTuple.setInvitingPlayerResult(100L);
        userResultTuple.setInvitedPlayerResult(200L);

        UserResultTupleDTO userResultTupleDTO = new UserResultTupleDTO();
        userResultTupleDTO.setGameId(userResultTuple.getGameId());
        userResultTupleDTO.setInvitingPlayerId(userResultTuple.getInvitingPlayerId());
        userResultTupleDTO.setInvitingPlayerResult(userResultTuple.getInvitingPlayerResult());
        userResultTupleDTO.setInvitedPlayerId(userResultTuple.getInvitedPlayerId());
        userResultTupleDTO.setInvitedPlayerResult(userResultTuple.getInvitedPlayerResult());

        List<UserResultTupleDTO> userResultTupleDTOs = new ArrayList<>();
        userResultTupleDTOs.add(userResultTupleDTO);

        given(userService.verifyToken(invitingUser.getToken())).willReturn(invitingUser);
        given(gameControllerService.intermediateResults(game.getGameId())).willReturn(userResultTupleDTOs);

        MockHttpServletRequestBuilder getRequest = get("/games/" + game.getGameId() + "/intermediate")
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", invitingUser.getToken());

        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].gameId", is((int) game.getGameId())))
                .andExpect(jsonPath("$[0].invitingPlayerId", is((int) invitingUser.getId())))
                .andExpect(jsonPath("$[0].invitingPlayerResult", is((int) userResultTuple.getInvitingPlayerResult())))
                .andExpect(jsonPath("$[0].invitedPlayerId", is((int) game.getInvitedUserId())))
                .andExpect(jsonPath("$[0].invitedPlayerResult", is((int) userResultTuple.getInvitedPlayerResult())));
    }

    @Test
    void intermediateGame_whenInvalidToken_throwsUnauthorized_401() throws Exception {
        invitingUser.setStatus(UserStatus.IN_GAME);

        UserResultTuple userResultTuple = new UserResultTuple(game.getGameId(), invitingUser.getId(), game.getInvitedUserId());
        userResultTuple.setInvitingPlayerResult(100L);
        userResultTuple.setInvitedPlayerResult(200L);

        UserResultTupleDTO userResultTupleDTO = new UserResultTupleDTO();
        userResultTupleDTO.setGameId(userResultTuple.getGameId());
        userResultTupleDTO.setInvitingPlayerId(userResultTuple.getInvitingPlayerId());
        userResultTupleDTO.setInvitingPlayerResult(userResultTuple.getInvitingPlayerResult());
        userResultTupleDTO.setInvitedPlayerId(userResultTuple.getInvitedPlayerId());
        userResultTupleDTO.setInvitedPlayerResult(userResultTuple.getInvitedPlayerResult());

        List<UserResultTupleDTO> userResultTupleDTOs = new ArrayList<>();
        userResultTupleDTOs.add(userResultTupleDTO);

        given(userService.verifyToken(invitingUser.getToken())).willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Provided token is invalid."));
        given(gameControllerService.intermediateResults(game.getGameId())).willReturn(userResultTupleDTOs);

        MockHttpServletRequestBuilder getRequest = get("/games/" + game.getGameId() + "/intermediate")
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", invitingUser.getToken());

        mockMvc.perform(getRequest).andExpect(status().isUnauthorized());
    }

    @Test
    void answerQuestion_whenAnswerToLate_thenReturnCorrectAnswer() throws Exception {
        AnswerDTO answerDTO = new AnswerDTO();
        answerDTO.setUserId(invitingUser.getId());
        answerDTO.setQuestionId(question.getQuestionId());
        answerDTO.setAnswerString(question.getCorrectAnswer());
        answerDTO.setAnsweredTime(112L);

        question.setCreationTime(new Date(new Date().getTime() - 30000));

        given(userService.verifyToken(invitingUser.getToken())).willReturn(invitingUser);
        given(gameControllerService.answerQuestion(Mockito.any(Answer.class))).willReturn(question.getCorrectAnswer());

        MockHttpServletRequestBuilder putRequest = put("/games/" + game.getGameId() + "/question")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(answerDTO))
                .header("token", invitingUser.getToken());

        mockMvc.perform(putRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.correctAnswer", is(question.getCorrectAnswer())));

        verify(gameControllerService).answerQuestion(Mockito.any(Answer.class));
    }

    @Test
    void getAllUsers_validInput_success_200() throws Exception {
      UserResultTupleDTO userResultTupleDTO = new UserResultTupleDTO();
      userResultTupleDTO.setGameId(game.getGameId());
      userResultTupleDTO.setInvitingPlayerId(game.getInvitingUserId());
      userResultTupleDTO.setInvitedPlayerId(game.getInvitedUserId());
      userResultTupleDTO.setInvitingPlayerResult(4000L);
      userResultTupleDTO.setInvitedPlayerResult(8000L);

      given(userService.verifyToken(invitingUser.getToken())).willReturn(invitingUser);
      given(gameControllerService.getAllUsersOfGame(game.getGameId())).willReturn(userResultTupleDTO);

      MockHttpServletRequestBuilder getRequest = get("/games/" + game.getGameId() + "/users")
              .contentType(MediaType.APPLICATION_JSON)
              .header("token", invitingUser.getToken());

      mockMvc.perform(getRequest)
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.gameId", is((int) game.getGameId())))
              .andExpect(jsonPath("$.invitingPlayerId", is((int) game.getInvitingUserId())))
              .andExpect(jsonPath("$.invitedPlayerId", is((int) game.getInvitedUserId())))
              .andExpect(jsonPath("$.invitingPlayerResult", is(4000)))
              .andExpect(jsonPath("$.invitedPlayerResult", is(8000)));
    }

    @Test
    void terminateGame_success() throws Exception {
        given(gameControllerService.searchGame(game.getGameId())).willReturn(game);

        MockHttpServletRequestBuilder postRequest = post("/games/" + game.getGameId() + "/termination")
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", invitingUser.getToken());

        mockMvc.perform(postRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId", is((int) game.getGameId())))
                .andExpect(jsonPath("$.invitedUserId", is((int) game.getInvitedUserId())))
                .andExpect(jsonPath("$.invitingUserId", is((int) game.getInvitingUserId())))
                .andExpect(jsonPath("$.modeType", is(game.getModeType().toString())))
                .andExpect(jsonPath("$.quizType", is(game.getQuizType().toString())));

        verify(webSocketController).informUsersGameDeleted(game.getGameId());
    }

    @Test
    void terminateFinishedGame_success() throws Exception {
        MockHttpServletRequestBuilder deleteRequest = delete("/games/" + game.getGameId() + "/finish")
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", invitingUser.getToken());

        mockMvc.perform(deleteRequest)
                .andExpect(status().isOk());

        verify(gameControllerService, times(1)).setInGamePlayersToOnline(game.getGameId());
    }

  @Test
  void createImageQuestion_thenQuestionCreated_201() throws Exception {
    QuestionDTO questionDTO = new QuestionDTO();
    questionDTO.setGameId(game.getGameId());

    given(userService.verifyToken(Mockito.any())).willReturn(invitingUser);
    given(gameControllerService.getImageQuestion(questionDTO.getGameId())).willReturn(imageQuestion);


    MockHttpServletRequestBuilder postRequest = post("/games/images")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(questionDTO))
            .header("token", "helloToken");

    mockMvc.perform(postRequest)
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.gameId", is((int) game.getGameId())))
            .andExpect(jsonPath("$.questionId", is((int) imageQuestion.getQuestionId())))
            .andExpect(jsonPath("$.correctAnswer").doesNotExist())
            .andExpect(jsonPath("$.incorrectAnswers").doesNotExist())
            .andExpect(jsonPath("$.allAnswers", containsInAnyOrder("me", "you", "allOfUs", "WhoKnows?")))
            .andExpect(jsonPath("$.question", is(imageQuestion.getQuestionString())));
  }

  @Test
  void createImageQuestion_invalidToken_throwsUnauthorized_401() throws Exception {
    QuestionDTO questionDTO = new QuestionDTO();
    questionDTO.setGameId(game.getGameId());

    given(userService.verifyToken(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Provided token is invalid."));
    given(gameControllerService.getImageQuestion(questionDTO.getGameId())).willReturn(imageQuestion);


    MockHttpServletRequestBuilder postRequest = post("/games/images")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(questionDTO))
            .header("token", "helloToken");

    mockMvc.perform(postRequest).andExpect(status().isUnauthorized());
  }





  private String asJsonString(final Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
              format("The request body could not be created.%s", e.toString()));
    }
  }
}
