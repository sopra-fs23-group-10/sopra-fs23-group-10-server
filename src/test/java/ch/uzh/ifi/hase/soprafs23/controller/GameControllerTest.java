package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.Category;
import ch.uzh.ifi.hase.soprafs23.constant.ModeType;
import ch.uzh.ifi.hase.soprafs23.constant.QuizType;
import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Question;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GameDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.QuestionDTO;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import ch.uzh.ifi.hase.soprafs23.service.WebSocketService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static java.lang.String.format;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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


    @Test
    void createQuestion_thenQuestionCreated_201() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUsername");
        user.setPassword("testPassword");
        user.setPoints(2L);
        user.setEmail("email@email.com");
        user.setProfilePicture("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);

        Game game = new Game(0, 0, 1, QuizType.TEXT, ModeType.DUEL);

        Question question = new Question();
        question.setId("62433573cfaae40c129614a9");
        question.setQuestion("Who wrote this test?");
        question.setCategory(Category.GENERAL_KNOWLEDGE);
        String[] answers = {"me", "you", "allOfUs", "WhoKnows?"};
        question.setAllAnswers(answers);
        String[] incorrectAnswers = {"you", "allOfUs", "WhoKnows?"};
        question.setIncorrectAnswers(incorrectAnswers);
        question.setCorrectAnswer("me");

        GameDTO gameDTO = new GameDTO();
        gameDTO.setId(game.getId());
        gameDTO.setInvitedUserId(game.getInvitedUserId());
        gameDTO.setInvitingUserId(game.getInvitingUserId());
        gameDTO.setModeType(game.getModeType());
        gameDTO.setQuizType(game.getQuizType());

        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setCategory(Category.GENERAL_KNOWLEDGE);
        questionDTO.setGameId(0);

        given(userService.verifyToken(Mockito.any())).willReturn(user);
        given(gameService.getQuestion(questionDTO.getCategory(), questionDTO.getGameId(), userService)).willReturn(question);


        MockHttpServletRequestBuilder postRequest = post("/game/topics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(questionDTO))
                .header("token", "helloToken");

        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.gameId", is((int) gameDTO.getId())))
                .andExpect(jsonPath("$.category", is(questionDTO.getCategory().toString())))
                .andExpect(jsonPath("$.id", is(question.getId())))
                .andExpect(jsonPath("$.correctAnswer").value(nullValue()))
                .andExpect(jsonPath("$.incorrectAnswers").value(nullValue()))
                .andExpect(jsonPath("$.allAnswers", containsInAnyOrder("me", "you", "allOfUs", "WhoKnows?")))
                .andExpect(jsonPath("$.question", is(question.getQuestion())));
    }






    /*
    @Test
    public void answerQuestion_whenQuestionNotAnswered_thenReturnTuple_200() throws Exception {
        // given
        Game game = new Game(1L, 1L, 2L, quizType, "DUEL");
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("testUsername");
        user1.setPassword("testPassword");
        user1.setPoints(2L);
        user1.setEmail("email@email.com");
        user1.setProfilePicture("testUsername");
        user1.setToken("1");
        user1.setStatus(UserStatus.IN_GAME);
        UserAnswerDTO userAnswerDTO = new UserAnswerDTO();
        userAnswerDTO.setUserId(user1.getId());
        userAnswerDTO.setQuestionId("questionID");
        userAnswerDTO.setAnswer("someAnswer");
        userAnswerDTO.setAnsweredTime(112L);
        games.put(game.getId(), game);
        // set up userService to throw the exception
        given(userService.verifyToken(Mockito.any())).willReturn(user1);
        // when/then -> build the post request
        MockHttpServletRequestBuilder postRequest = put("/game/question/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userAnswerDTO));
        // then
        mockMvc.perform(postRequest).andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId", is(user1.getId())))
                .andExpect(jsonPath("$.questionId", is(userAnswerDTO.getQuestionId())))
                .andExpect(jsonPath("$.answer", is(userAnswerDTO.getAnswer())))
                .andExpect(jsonPath("$.answeredTime", is(userAnswerDTO.getAnsweredTime().toString())));
    }*/
/*
    @Test
    public void finishGame_whenPointsUpdated_thenUserResultTupleDTO_200() throws Exception {
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
        UserResultTuple userResultTuple = new UserResultTuple(1L,1L,2L);
        userResultTuple.setInvitingPlayerResult(1L);
        userResultTuple.setInvitedPlayerResult(1L);
        given(userService.verifyToken(user1.getToken())).willReturn(user1);
        given(userService.verifyToken(user2.getToken())).willReturn(user2);
        given(userService.getPoints(Mockito.any())).willReturn(3L);
        // when/then -> build the post request
        MockHttpServletRequestBuilder deleteRequest = delete("/game/finish/"+game.getId());
        // then
        mockMvc.perform(deleteRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId", is(game.getId())))
                .andExpect(jsonPath("$.invitingPlayerId", is(user1.getId())))
                .andExpect(jsonPath("$.invitingPlayerResult", is(4L)))
                .andExpect(jsonPath("$.invitedPlayerId", is(user2.getId())))
                .andExpect(jsonPath("$.invitedPlayerResult", is(4L)));
    }*/
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
    }
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    format("The request body could not be created.%s", e.toString()));
        }
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
