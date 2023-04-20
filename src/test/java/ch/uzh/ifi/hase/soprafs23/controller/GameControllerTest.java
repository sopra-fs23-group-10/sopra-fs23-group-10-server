package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.Category;
import ch.uzh.ifi.hase.soprafs23.constant.ModeType;
import ch.uzh.ifi.hase.soprafs23.constant.QuizType;
import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GameDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.QuestionDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserAnswerDTO;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
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
    private GameService gameService;

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

    private User invitingUser;
    private Question question;
    private Game game;

    @BeforeEach
    void setup() {
        invitingUser = new User();
        invitingUser.setId(1L);
        invitingUser.setUsername("testUsername");
        invitingUser.setPassword("testPassword");
        invitingUser.setPoints(2L);
        invitingUser.setEmail("email@email.com");
        invitingUser.setProfilePicture("testUsername");
        invitingUser.setBackgroundMusic(false);
        invitingUser.setToken("inviting");
        invitingUser.setStatus(UserStatus.ONLINE);

        game = new Game(0L, invitingUser.getId(), 2L, QuizType.TEXT, ModeType.DUEL);

        question = new Question();
        question.setId("62433573cfaae40c129614a9");
        question.setQuestion("Who wrote this test?");
        question.setCategory(Category.GENERAL_KNOWLEDGE);
        String[] answers = {"me", "you", "allOfUs", "WhoKnows?"};
        question.setAllAnswers(answers);
        String[] incorrectAnswers = {"you", "allOfUs", "WhoKnows?"};
        question.setIncorrectAnswers(incorrectAnswers);
        question.setCorrectAnswer("me");
    }

    @Test
    void createGame_thenGameReturned_201() throws Exception {
        User invitedUser = new User();
        invitedUser.setId(game.getInvitedUserId());
        invitedUser.setUsername("invitedUser");
        invitedUser.setPassword("testPassword");
        invitedUser.setPoints(2L);
        invitedUser.setEmail("invitedUser@email.com");
        invitedUser.setProfilePicture("invitedUser");
        invitedUser.setBackgroundMusic(false);
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
        given(gameService.createGame(invitingUser.getId(), invitedUser.getId(), gameDTO.getQuizType(), gameDTO.getModeType())).willReturn(game);

        MockHttpServletRequestBuilder postRequest = post("/game/creation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gameDTO))
                .header("token", invitingUser.getToken());

        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is((int) game.getId())))
                .andExpect(jsonPath("$.invitedUserId", is((int) game.getInvitedUserId())))
                .andExpect(jsonPath("$.invitingUserId", is((int) game.getInvitingUserId())))
                .andExpect(jsonPath("$.modeType", is(game.getModeType().toString())))
                .andExpect(jsonPath("$.quizType", is(game.getQuizType().toString())));
    }

    @Test
    void createGame_invalidToken_throwsUnauthorized_401() throws Exception {
        User invitedUser = new User();
        invitedUser.setId(game.getInvitedUserId());
        invitedUser.setUsername("invitedUser");
        invitedUser.setPassword("testPassword");
        invitedUser.setPoints(2L);
        invitedUser.setEmail("invitedUser@email.com");
        invitedUser.setProfilePicture("invitedUser");
        invitedUser.setBackgroundMusic(false);
        invitedUser.setToken("invited");
        invitedUser.setStatus(UserStatus.ONLINE);

        GameDTO gameDTO = new GameDTO();
        gameDTO.setInvitingUserId(game.getInvitingUserId());
        gameDTO.setInvitedUserId(game.getInvitedUserId());
        gameDTO.setQuizType(QuizType.TEXT);
        gameDTO.setModeType(ModeType.DUEL);

        given(userService.verifyToken(invitingUser.getToken())).willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Provided token is invalid."));
        given(userService.searchUserById(gameDTO.getInvitedUserId())).willReturn(invitedUser);
        given(userService.searchUserById(gameDTO.getInvitingUserId())).willReturn(invitingUser);
        given(gameService.createGame(invitingUser.getId(), invitedUser.getId(), gameDTO.getQuizType(), gameDTO.getModeType())).willReturn(game);

        MockHttpServletRequestBuilder postRequest = post("/game/creation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gameDTO))
                .header("token", invitingUser.getToken());

        mockMvc.perform(postRequest).andExpect(status().isUnauthorized());
    }

    @Test
    void createQuestion_thenQuestionCreated_201() throws Exception {
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setCategory(question.getCategory());
        questionDTO.setGameId(game.getId());

        given(userService.verifyToken(Mockito.any())).willReturn(invitingUser);
        given(gameService.getQuestion(questionDTO.getCategory(), questionDTO.getGameId())).willReturn(question);


        MockHttpServletRequestBuilder postRequest = post("/game/topics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(questionDTO))
                .header("token", "helloToken");

        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.gameId", is((int) game.getId())))
                .andExpect(jsonPath("$.category", is(question.getCategory().toString())))
                .andExpect(jsonPath("$.id", is(question.getId())))
                .andExpect(jsonPath("$.correctAnswer").value(nullValue()))
                .andExpect(jsonPath("$.incorrectAnswers").value(nullValue()))
                .andExpect(jsonPath("$.allAnswers", containsInAnyOrder("me", "you", "allOfUs", "WhoKnows?")))
                .andExpect(jsonPath("$.question", is(question.getQuestion())));
    }

    @Test
    void createQuestion_invalidToken_throwsUnauthorized_401() throws Exception {
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setCategory(question.getCategory());
        questionDTO.setGameId(game.getId());

        given(userService.verifyToken(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Provided token is invalid."));
        given(gameService.getQuestion(questionDTO.getCategory(), questionDTO.getGameId())).willReturn(question);


        MockHttpServletRequestBuilder postRequest = post("/game/topics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(questionDTO))
                .header("token", "helloToken");

        mockMvc.perform(postRequest).andExpect(status().isUnauthorized());
    }

    @Test
    public void getTopicSelection_whenValid_thenReturnTopics_200() throws Exception {
        List<Category> categories = new ArrayList<>();
        categories.add(Category.MUSIC);
        categories.add(Category.FILM_TV);
        categories.add(Category.GENERAL_KNOWLEDGE);

        given(userService.verifyToken(invitingUser.getToken())).willReturn(invitingUser);
        given(gameService.getRandomTopics(0L, invitingUser.getId())).willReturn(Collections.singletonMap("topics", categories));

        MockHttpServletRequestBuilder getRequest = get("/game/topics/" + game.getId())
                .header("token", invitingUser.getToken());

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.topics[0]", is(categories.get(0).toString())))
                .andExpect(jsonPath("$.topics[1]", is(categories.get(1).toString())))
                .andExpect(jsonPath("$.topics[2]", is(categories.get(2).toString())));
    }

    @Test
    public void getTopicSelection_whenInvalidToken_thenThrowUnauthorized_401() throws Exception {
        List<Category> categories = new ArrayList<>();
        categories.add(Category.MUSIC);
        categories.add(Category.FILM_TV);
        categories.add(Category.GENERAL_KNOWLEDGE);

        String invalidToken = "someInvalidToken";

        when(userService.verifyToken(invalidToken)).thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Provided token is invalid."));
        given(userService.searchUserById(invitingUser.getId())).willReturn(invitingUser);

        MockHttpServletRequestBuilder getRequest = get("/game/topics/" + game.getId())
                .header("token", invalidToken);

        mockMvc.perform(getRequest).andExpect(status().isUnauthorized());
    }
/*
    @Test
    public void answerQuestion_whenQuestionNotAnswered_thenReturnBoolean_201() throws Exception {
        UserAnswerDTO userAnswerDTO = new UserAnswerDTO();
        userAnswerDTO.setUserId(invitingUser.getId());
        userAnswerDTO.setQuestionId(question.getId());
        userAnswerDTO.setAnswer(question.getCorrectAnswer());
        userAnswerDTO.setAnsweredTime(112L);

        Map<String, Boolean> booleanMap = Collections.singletonMap("boolean", userAnswerDTO.getAnswer().equals(question.getCorrectAnswer()));

        given(userService.verifyToken(Mockito.any())).willReturn(invitingUser);
        given(gameService.answerQuestion(Mockito.any(Long.class), Mockito.any(UserAnswerTuple.class), Mockito.any(WebSocketController.class))).willReturn(booleanMap);

        MockHttpServletRequestBuilder putRequest = put("/game/question/" + game.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userAnswerDTO))
                .header("token", invitingUser.getToken());

        mockMvc.perform(putRequest).andExpect(status().isCreated())
                .andExpect(jsonPath("$.boolean", is((boolean) booleanMap.get("boolean"))));
    }*/
/*
    @Test
    public void answerQuestion_whenNullAnswer_thenNotAcceptable_406() throws Exception {
        UserAnswerDTO userAnswerDTO = new UserAnswerDTO();
        userAnswerDTO.setUserId(null);
        userAnswerDTO.setQuestionId(null);
        userAnswerDTO.setAnswer(null);
        userAnswerDTO.setAnsweredTime(null);

        given(userService.verifyToken(Mockito.any())).willReturn(invitingUser);
        doThrow(new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "The received answer cannot be null.")).when(gameService).answerQuestion(Mockito.any(Long.class), Mockito.any(UserAnswerTuple.class));
        //given(gameService.answerQuestion(Mockito.any(Long.class), Mockito.any(UserAnswerTuple.class))).willThrow(new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "The received answer cannot be null."));
        given(gameService.answerQuestion(Mockito.any(Long.class), Mockito.any(UserAnswerTuple.class))).

        MockHttpServletRequestBuilder putRequest = put("/game/question/" + game.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userAnswerDTO))
                .header("token", invitingUser.getToken());

        mockMvc.perform(putRequest).andExpect(status().isNotAcceptable());
    }

    @Test
    public void answerQuestion_alreadyAnswered_thenConflict_409() throws Exception {
        UserAnswerDTO userAnswerDTO = new UserAnswerDTO();
        userAnswerDTO.setUserId(invitingUser.getId());
        userAnswerDTO.setQuestionId(question.getId());
        userAnswerDTO.setAnswer(question.getAllAnswers()[2]);
        userAnswerDTO.setAnsweredTime(112L);

        given(userService.verifyToken(Mockito.any())).willReturn(invitingUser);
        //given(gameService.answerQuestion(Mockito.any(Long.class), Mockito.any(UserAnswerTuple.class), Mockito.any(WebSocketController.class))).willThrow(new ResponseStatusException(HttpStatus.CONFLICT, "User has already answered this question."));

        MockHttpServletRequestBuilder putRequest = put("/game/question/" + game.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userAnswerDTO))
                .header("token", invitingUser.getToken());

        mockMvc.perform(putRequest).andExpect(status().isConflict());
    }
*/
/*
    @Test
    public void finishGame_whenPointsUpdated_thenUserResultTupleDTO_200() throws Exception {
        invitingUser.setStatus(UserStatus.IN_GAME);

        User invitedUser = new User();
        invitedUser.setId(game.getInvitedUserId());
        invitedUser.setUsername("invitedUser");
        invitedUser.setPassword("testPassword");
        invitedUser.setPoints(2L);
        invitedUser.setEmail("invitedUser@email.com");
        invitedUser.setProfilePicture("invitedUser");
        invitedUser.setBackgroundMusic(false);
        invitedUser.setToken("invited");
        invitedUser.setStatus(UserStatus.IN_GAME);

        UserResultTuple userResultTuple = new UserResultTuple(game.getId(), invitingUser.getId(), invitedUser.getId());
        userResultTuple.setInvitingPlayerResult(100L);
        userResultTuple.setInvitedPlayerResult(200L);

        given(userService.verifyToken(invitingUser.getToken())).willReturn(invitingUser);
        given(gameService.finishGame(game.getId())).willReturn(userResultTuple);

        MockHttpServletRequestBuilder deleteRequest = delete("/game/finish/"+game.getId())
                .header("token", invitingUser.getToken());

        mockMvc.perform(deleteRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId", is((int) game.getId())))
                .andExpect(jsonPath("$.invitingPlayerId", is((int) invitingUser.getId())))
                .andExpect(jsonPath("$.invitingPlayerResult", is((int) userResultTuple.getInvitingPlayerResult())))
                .andExpect(jsonPath("$.invitedPlayerId", is((int) invitedUser.getId())))
                .andExpect(jsonPath("$.invitedPlayerResult", is((int) userResultTuple.getInvitedPlayerResult())));
    }

    @Test
    public void finishGame_whenInvalidToken_thenThrowUnautorized_401() throws Exception {
        invitingUser.setStatus(UserStatus.IN_GAME);

        User invitedUser = new User();
        invitedUser.setId(game.getInvitedUserId());
        invitedUser.setUsername("invitedUser");
        invitedUser.setPassword("testPassword");
        invitedUser.setPoints(2L);
        invitedUser.setEmail("invitedUser@email.com");
        invitedUser.setProfilePicture("invitedUser");
        invitedUser.setBackgroundMusic(false);
        invitedUser.setToken("invited");
        invitedUser.setStatus(UserStatus.IN_GAME);

        UserResultTuple userResultTuple = new UserResultTuple(game.getId(), invitingUser.getId(), invitedUser.getId());
        userResultTuple.setInvitingPlayerResult(100L);
        userResultTuple.setInvitedPlayerResult(200L);

        given(userService.verifyToken(invitingUser.getToken())).willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Provided token is invalid."));
        given(gameService.finishGame(game.getId())).willReturn(userResultTuple);

        MockHttpServletRequestBuilder deleteRequest = delete("/game/finish/"+game.getId())
                .header("token", invitingUser.getToken());

        mockMvc.perform(deleteRequest).andExpect(status().isUnauthorized());
    }
    */
/*
    @Test
    public void intermediateGame_whenValid_returnResults_200() throws Exception {
        invitingUser.setStatus(UserStatus.IN_GAME);

        User invitedUser = new User();
        invitedUser.setId(game.getInvitedUserId());
        invitedUser.setUsername("invitedUser");
        invitedUser.setPassword("testPassword");
        invitedUser.setPoints(2L);
        invitedUser.setEmail("invitedUser@email.com");
        invitedUser.setProfilePicture("invitedUser");
        invitedUser.setBackgroundMusic(false);
        invitedUser.setToken("invited");
        invitedUser.setStatus(UserStatus.IN_GAME);

        UserResultTuple userResultTuple = new UserResultTuple(game.getId(), invitingUser.getId(), invitedUser.getId());
        userResultTuple.setInvitingPlayerResult(100L);
        userResultTuple.setInvitedPlayerResult(200L);

        given(userService.verifyToken(invitingUser.getToken())).willReturn(invitingUser);
        given(gameService.intermediateResults(game.getId())).willReturn(userResultTuple);

        MockHttpServletRequestBuilder getRequest = get("/game/intermediate/" + game.getId())
                .header("token", invitingUser.getToken());

        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId", is((int) game.getId())))
                .andExpect(jsonPath("$.invitingPlayerId", is((int) invitingUser.getId())))
                .andExpect(jsonPath("$.invitingPlayerResult", is((int) userResultTuple.getInvitingPlayerResult())))
                .andExpect(jsonPath("$.invitedPlayerId", is((int) invitedUser.getId())))
                .andExpect(jsonPath("$.invitedPlayerResult", is((int) userResultTuple.getInvitedPlayerResult())));
    }
    */

    /*
    @Test
    public void intermediateGame_whenInvalidToken_throwsUnauthorized_401() throws Exception {
        invitingUser.setStatus(UserStatus.IN_GAME);

        User invitedUser = new User();
        invitedUser.setId(game.getInvitedUserId());
        invitedUser.setUsername("invitedUser");
        invitedUser.setPassword("testPassword");
        invitedUser.setPoints(2L);
        invitedUser.setEmail("invitedUser@email.com");
        invitedUser.setProfilePicture("invitedUser");
        invitedUser.setBackgroundMusic(false);
        invitedUser.setToken("invited");
        invitedUser.setStatus(UserStatus.IN_GAME);

        UserResultTuple userResultTuple = new UserResultTuple(game.getId(), invitingUser.getId(), invitedUser.getId());
        userResultTuple.setInvitingPlayerResult(100L);
        userResultTuple.setInvitedPlayerResult(200L);

        given(userService.verifyToken(invitingUser.getToken())).willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Provided token is invalid."));
        given(gameService.intermediateResults(game.getId())).willReturn(userResultTuple);

        MockHttpServletRequestBuilder getRequest = get("/game/intermediate/" + game.getId())
                .header("token", invitingUser.getToken());

        mockMvc.perform(getRequest).andExpect(status().isUnauthorized());
    }
    */
/*
    @Test
    public void finishGame_whenPointsUpdated_thenUserResultTupleDTO_200() throws Exception {
        // given
        Game game = new Game(1L, 1L, 2L, QuizType.TEXT, ModeType.DUEL);
        games.put(game.getId(), game);
        //inviting User
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("testUsername");
        user1.setPassword("testPassword");
        user1.setPoints(3L);
        user1.setEmail("email@email.com");
        user1.setProfilePicture("testUsername");
        user1.setToken("1");
        user1.setStatus(UserStatus.IN_GAME);
        //invited User
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("test");
        user2.setPassword("testPW");
        user2.setPoints(3L);
        user2.setEmail("test@test.com");
        user2.setProfilePicture("test");
        user2.setToken("2");
        user2.setStatus(UserStatus.IN_GAME);
        Question question = new Question();
        question.setCategory(Category.ARTS_LITERATURE);
        question.setId("1");
        question.setCorrectAnswer("True");
        String[] incorrectAnswer = new String[1];
        incorrectAnswer[0] = "False";
        question.setIncorrectAnswers(incorrectAnswer);
        String[] allAnswer = {"False","True"};
        question.setAllAnswers(allAnswer);
        question.setQuestion("Is the sky blue?");
        UserAnswerTuple answerTuple1 = new UserAnswerTuple();
        answerTuple1.setUserId(user1.getId());
        answerTuple1.setQuestionId(question.getId());
        answerTuple1.setAnswer("True");
        answerTuple1.setAnsweredTime(1L);
        UserAnswerTuple answerTuple2 = new UserAnswerTuple();
        answerTuple2.setUserId(user2.getId());
        answerTuple2.setQuestionId(question.getId());
        answerTuple2.setAnswer("False");
        answerTuple2.setAnsweredTime(1L);
        question.addAnswer(answerTuple1);
        question.addAnswer(answerTuple2);
        UserResultTuple userResultTuple = game.getResults();
        // set up userService to throw the exception
        given(userService.verifyToken(user1.getToken())).willReturn(user1);
        given(userService.verifyToken(user2.getToken())).willReturn(user2);
        given(userService.getPoints(Mockito.any())).willReturn(3L);
        //given(webSocketController.endResultToUser(Mockito.any(), Mockito.any()));
        // when/then -> build the post request
        MockHttpServletRequestBuilder deleteRequest = delete("/game/finish/"+game.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(""));
        // then
        mockMvc.perform(deleteRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId", is(game.getId())))
                .andExpect(jsonPath("$.invitingPlayerId", is(user1.getId())))
                .andExpect(jsonPath("$.invitingPlayerResult", is(user1.getPoints()+ userResultTuple.getInvitingPlayerResult())))
                .andExpect(jsonPath("$.invitedPlayerId", is(user2.getId())))
                .andExpect(jsonPath("$.invitedPlayerResult", is(user2.getPoints()+ userResultTuple.getInvitedPlayerResult())));
    }*/


    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    format("The request body could not be created.%s", e.toString()));
        }
    }

}
