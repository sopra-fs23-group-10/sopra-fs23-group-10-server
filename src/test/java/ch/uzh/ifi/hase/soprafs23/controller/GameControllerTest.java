package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.Category;
import ch.uzh.ifi.hase.soprafs23.constant.ModeType;
import ch.uzh.ifi.hase.soprafs23.constant.QuizType;
import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserAnswerDTO;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;

import static java.lang.String.format;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameController.class)
public class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;
    @MockBean
    private UserService userService;
    private final HashMap<Long, Game> games = new HashMap<>();

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
}
