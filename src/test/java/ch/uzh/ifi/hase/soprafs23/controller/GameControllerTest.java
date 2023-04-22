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

    private User invitingUser;
    private Question question;
    private Game game;

    @BeforeEach
    void setup() {
        invitingUser = new User();
        invitingUser.setId(1L);
        invitingUser.setToken("inviting");
        invitingUser.setStatus(UserStatus.ONLINE);

        game = new Game();
        game.setGameId(0L);
        game.setInvitingUserId(invitingUser.getId());
        game.setInvitedUserId(2L);
        game.setQuizType(QuizType.TEXT);
        game.setModeType(ModeType.DUEL);

        question = new Question();
        question.setQuestionId(69L);
        question.setApiId("62433573cfaae40c129614a9");
        question.setQuestion("Who wrote this test?");
        question.setCategory(Category.GENERAL_KNOWLEDGE);
        String[] answers = {"me", "you", "allOfUs", "WhoKnows?"};
        question.setAllAnswers(List.of(answers));
        String[] incorrectAnswers = {"you", "allOfUs", "WhoKnows?"};
        question.setIncorrectAnswers(List.of(incorrectAnswers));
        question.setCorrectAnswer("me");
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

        MockHttpServletRequestBuilder postRequest = post("/game/creation")
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
        questionDTO.setGameId(game.getGameId());

        given(userService.verifyToken(Mockito.any())).willReturn(invitingUser);
        given(gameControllerService.getQuestion(questionDTO.getCategory(), questionDTO.getGameId())).willReturn(question);


        MockHttpServletRequestBuilder postRequest = post("/game/topics")
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
                .andExpect(jsonPath("$.question", is(question.getQuestion())));
    }

    @Test
    void createQuestion_invalidToken_throwsUnauthorized_401() throws Exception {
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setCategory(question.getCategory());
        questionDTO.setGameId(game.getGameId());

        given(userService.verifyToken(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Provided token is invalid."));
        given(gameControllerService.getQuestion(questionDTO.getCategory(), questionDTO.getGameId())).willReturn(question);


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
        given(gameControllerService.getRandomTopics(0L, invitingUser.getId())).willReturn(Collections.singletonMap("topics", categories));

        MockHttpServletRequestBuilder getRequest = get("/game/topics/" + game.getGameId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", invitingUser.getToken());

        mockMvc.perform(getRequest).andExpect(status().isOk())
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

        MockHttpServletRequestBuilder getRequest = get("/game/topics/" + game.getGameId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", invalidToken);

        mockMvc.perform(getRequest).andExpect(status().isUnauthorized());
    }

    //TODO: This test does not do much yet
    @Test
    public void getAllTopics_whenValid_thenSuccess_200() throws Exception {
        Map<String, List<Category>> topics = Collections.singletonMap("topics", new ArrayList<>(Arrays.asList(Category.values())));

        given(userService.verifyToken(invitingUser.getToken())).willReturn(invitingUser);
        given(gameControllerService.getAllTopics()).willReturn(topics);

        MockHttpServletRequestBuilder getRequest = get("/game/topics/all")
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", invitingUser.getToken());

        mockMvc.perform(getRequest).andExpect(status().isOk());
    }

    @Test
    public void answerQuestion_whenQuestionNotAnswered_thenSuccess_201() throws Exception {
        AnswerDTO answerDTO = new AnswerDTO();
        answerDTO.setUserId(invitingUser.getId());
        answerDTO.setQuestionId(question.getQuestionId());
        answerDTO.setAnswer(question.getCorrectAnswer());
        answerDTO.setAnsweredTime(112L);

        given(userService.verifyToken(Mockito.any())).willReturn(invitingUser);

        MockHttpServletRequestBuilder putRequest = put("/game/question/" + game.getGameId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(answerDTO))
                .header("token", invitingUser.getToken());

        mockMvc.perform(putRequest).andExpect(status().isCreated());
    }

    @Test
    public void answerQuestion_whenNullAnswer_thenNotAcceptable_406() throws Exception {
        AnswerDTO answerDTO = new AnswerDTO();

        given(userService.verifyToken(Mockito.any())).willReturn(invitingUser);
        Mockito.doThrow(new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "The received answer cannot be null.")).when(gameControllerService).answerQuestion(Mockito.any(Long.class), Mockito.any(Answer.class));


        MockHttpServletRequestBuilder putRequest = put("/game/question/" + game.getGameId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(answerDTO))
                .header("token", invitingUser.getToken());

        mockMvc.perform(putRequest).andExpect(status().isNotAcceptable());
    }

    @Test
    public void answerQuestion_alreadyAnswered_thenConflict_409() throws Exception {
        AnswerDTO answerDTO = new AnswerDTO();
        answerDTO.setUserId(invitingUser.getId());
        answerDTO.setQuestionId(question.getQuestionId());
        answerDTO.setAnswer(question.getAllAnswers().get(2));
        answerDTO.setAnsweredTime(112L);

        given(userService.verifyToken(Mockito.any())).willReturn(invitingUser);
        Mockito.doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "User has already answered this question.")).when(gameControllerService).answerQuestion(Mockito.any(Long.class), Mockito.any(Answer.class));

        MockHttpServletRequestBuilder putRequest = put("/game/question/" + game.getGameId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(answerDTO))
                .header("token", invitingUser.getToken());

        mockMvc.perform(putRequest).andExpect(status().isConflict());
    }

    @Test
    public void allUsersConnected_bothConnected_validInput_success() throws Exception {
        given(userService.verifyToken(invitingUser.getToken())).willReturn(invitingUser);
        given(gameControllerService.allUsersConnected(game.getGameId())).willReturn(Collections.singletonMap("status", true));

        MockHttpServletRequestBuilder getRequest = get("/game/online/" + game.getGameId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", invitingUser.getToken());

        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(true)));
    }

    @Test
    public void allUsersConnected_oneNotConnected_validInput_success() throws Exception {
        given(userService.verifyToken(invitingUser.getToken())).willReturn(invitingUser);
        given(gameControllerService.allUsersConnected(game.getGameId())).willReturn(Collections.singletonMap("status", false));

        MockHttpServletRequestBuilder getRequest = get("/game/online/" + game.getGameId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", invitingUser.getToken());

        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(false)));
    }

    @Test
    public void allUsersConnected_bothConnected_invalidToken_throws_401() throws Exception {
        given(userService.verifyToken(invitingUser.getToken())).willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Provided token is invalid."));
        given(gameControllerService.allUsersConnected(game.getGameId())).willReturn(Collections.singletonMap("status", true));

        MockHttpServletRequestBuilder getRequest = get("/game/online/" + game.getGameId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", invitingUser.getToken());

        mockMvc.perform(getRequest).andExpect(status().isUnauthorized());
    }

    @Test
    public void finishGame_whenPointsUpdated_thenUserResultTupleDTO_200() throws Exception {
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
        given(gameControllerService.finishGame(game.getGameId())).willReturn(userResultTupleDTOs);

        MockHttpServletRequestBuilder deleteRequest = delete("/game/finish/" + game.getGameId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", invitingUser.getToken());

        mockMvc.perform(deleteRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].gameId", is((int) game.getGameId())))
                .andExpect(jsonPath("$[0].invitingPlayerId", is((int) invitingUser.getId())))
                .andExpect(jsonPath("$[0].invitingPlayerResult", is((int) userResultTuple.getInvitingPlayerResult())))
                .andExpect(jsonPath("$[0].invitedPlayerId", is((int) game.getInvitedUserId())))
                .andExpect(jsonPath("$[0].invitedPlayerResult", is((int) userResultTuple.getInvitedPlayerResult())));
    }
/*
    @Test
    public void finishGame_whenInvalidToken_thenThrowUnautorized_401() throws Exception {
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
        given(gameService.finishGame(game.getGameId())).willReturn(userResultTupleDTOs);

        MockHttpServletRequestBuilder deleteRequest = delete("/game/finish/" + game.getGameId())
                .header("token", invitingUser.getToken());

        mockMvc.perform(deleteRequest).andExpect(status().isUnauthorized());
    }*/

    @Test
    public void intermediateGame_whenValid_returnResults_200() throws Exception {
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

        MockHttpServletRequestBuilder getRequest = get("/game/intermediate/" + game.getGameId())
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
    public void intermediateGame_whenInvalidToken_throwsUnauthorized_401() throws Exception {
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

        MockHttpServletRequestBuilder getRequest = get("/game/intermediate/" + game.getGameId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", invitingUser.getToken());

        mockMvc.perform(getRequest).andExpect(status().isUnauthorized());
    }
/*
    @Test
    public void finishGame_whenPointsUpdated_thenUserResultTupleDTO_200() throws Exception {
        // given
        Game game = new Game(1L, 1L, 2L, QuizType.TEXT, ModeType.DUEL);
        games.put(game.getGameId(), game);
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
        MockHttpServletRequestBuilder deleteRequest = delete("/game/finish/"+game.getGameId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(""));
        // then
        mockMvc.perform(deleteRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId", is(game.getGameId())))
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
