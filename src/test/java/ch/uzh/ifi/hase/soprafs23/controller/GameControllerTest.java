package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.User;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameController.class)
public class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;
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

    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    format("The request body could not be created.%s", e.toString()));
        }
    }
}
