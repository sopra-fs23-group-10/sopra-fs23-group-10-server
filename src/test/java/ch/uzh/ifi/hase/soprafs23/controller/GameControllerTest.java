package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.Category;
import ch.uzh.ifi.hase.soprafs23.constant.ModeType;
import ch.uzh.ifi.hase.soprafs23.constant.QuizType;
import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GameDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.QuestionDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserAnswerDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserResultTupleDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.String.format;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
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
        given(gameService.getQuestion(questionDTO.getCategory(), questionDTO.getGameId())).willReturn(question);


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

    @Test
    public void getTopicSelection_whenValid_thenReturnTopics_200() throws Exception {
        User user = new User();
        user.setId(2L);
        user.setUsername("testUsername");
        user.setPassword("testPassword");
        user.setPoints(2L);
        user.setEmail("email@email.com");
        user.setProfilePicture("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);

        Game game = new Game(0L, 1L, user.getId(), QuizType.TEXT, ModeType.DUEL);

        List<Category> categories = new ArrayList<>();
        categories.add(Category.MUSIC);
        categories.add(Category.FILM_TV);
        categories.add(Category.GENERAL_KNOWLEDGE);

        given(userService.verifyToken(user.getToken())).willReturn(user);
        given(gameService.getRandomTopics(0L, user.getId())).willReturn(Collections.singletonMap("topics", categories));

        MockHttpServletRequestBuilder getRequest = get("/game/topics/" + game.getId())
                .header("token", user.getToken());

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.topics[0]", is(categories.get(0).toString())))
                .andExpect(jsonPath("$.topics[1]", is(categories.get(1).toString())))
                .andExpect(jsonPath("$.topics[2]", is(categories.get(2).toString())));
    }

    @Test
    public void getTopicSelection_whenInvalidToken_thenThrowUnauthorized_401() throws Exception {
        User user = new User();
        user.setId(2L);
        user.setUsername("testUsername");
        user.setPassword("testPassword");
        user.setPoints(2L);
        user.setEmail("email@email.com");
        user.setProfilePicture("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);

        Game game = new Game(0L, 1L, user.getId(), QuizType.TEXT, ModeType.DUEL);

        List<Category> categories = new ArrayList<>();
        categories.add(Category.MUSIC);
        categories.add(Category.FILM_TV);
        categories.add(Category.GENERAL_KNOWLEDGE);

        String invalidToken = "someInvalidToken";

        when(userService.verifyToken(invalidToken)).thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Provided token is invalid."));
        given(userService.searchUserById(user.getId())).willReturn(user);

        MockHttpServletRequestBuilder getRequest = get("/game/topics/" + game.getId())
                .header("token", invalidToken);

        mockMvc.perform(getRequest).andExpect(status().isUnauthorized());
    }


    @Test
    public void answerQuestion_whenQuestionNotAnswered_thenReturnTuple_200() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setUsername("invitingUser");
        user.setPassword("testPassword");
        user.setPoints(2L);
        user.setEmail("invitor@email.com");
        user.setProfilePicture("invitingUser");
        user.setToken("1");
        user.setStatus(UserStatus.IN_GAME);
        
        Game game = new Game(1L, user.getId(), 2L, QuizType.TEXT, ModeType.DUEL);

        UserAnswerDTO userAnswerDTO = new UserAnswerDTO();
        userAnswerDTO.setUserId(user.getId());
        userAnswerDTO.setQuestionId("questionID");
        userAnswerDTO.setAnswer("someAnswer");
        userAnswerDTO.setAnsweredTime(112L);

        UserAnswerTuple userAnswerTuple = DTOMapper.INSTANCE.convertUserAnswerDTOtoEntity(userAnswerDTO);

        UserResultTuple userResultTuple = new UserResultTuple();
        userResultTuple.setGameId(game.getId());
        userResultTuple.setInvitingPlayerId(game.getInvitingUserId());
        userResultTuple.setInvitingPlayerResult(200L);
        userResultTuple.setInvitedPlayerId(game.getInvitedUserId());
        userResultTuple.setInvitedPlayerResult(0L);

        UserResultTupleDTO userResultTupleDTO = DTOMapper.INSTANCE.convertUserResultTupleEntitytoDTO(userResultTuple);

        // set up userService to throw the exception
        given(userService.verifyToken(Mockito.any())).willReturn(user);
        given(gameService.answerQuestion(game.getId(), userAnswerTuple, webSocketController)).willReturn(userResultTuple);
        // when/then -> build the post request
        MockHttpServletRequestBuilder putRequest = put("/game/question/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userAnswerDTO))
                .header("token", user.getToken());
        // then
        mockMvc.perform(putRequest).andExpect(status().isCreated())
                .andExpect(jsonPath("$.gameId").doesNotExist())
                .andExpect(jsonPath("$.invitingPlayerId").doesNotExist())
                .andExpect(jsonPath("$.invitingPlayerResult").doesNotExist())
                .andExpect(jsonPath("$.invitedPlayerId").doesNotExist())
                .andExpect(jsonPath("$.invitedPlayerResult").doesNotExist());
        //TODO: Check if assertions are correct
    }
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
