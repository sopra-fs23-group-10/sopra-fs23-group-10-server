package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.Category;
import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.handler.GameMap;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class QuestionTest {

    @InjectMocks
    private Question question;

    private User invitingUser;

    private User invitedUser;


    @BeforeEach
    public void setup() {
        this.question = new Question();
        String[] incorrectAnswers = {"Neil Young", "Eric Clapton", "Elton John"};
        String[] allAnswers = {"Neil Young", "Bob Dylan", "Eric Clapton", "Elton John"};

        question.setCategory(Category.MUSIC);
        question.setId("622a1c357cc59eab6f94ff56");
        question.setCorrectAnswer("Bob Dylan");
        question.setIncorrectAnswers(incorrectAnswers);
        question.setAllAnswers(allAnswers);
        question.setQuestion("Which musician has famously performed over 3,000 shows in their 'Never Ending Tour'?");

        Map<Long, UserAnswerTuple> resultsMap = new HashMap<>();
        question.setResults(resultsMap);

        this.invitingUser = new User();
        invitingUser.setId(1L);
        invitingUser.setUsername("oneUser");
        invitingUser.setPassword("testPassword");
        invitingUser.setPoints(2L);
        invitingUser.setEmail("oneUser@email.com");
        invitingUser.setProfilePicture("oneUser");
        invitingUser.setToken("1");
        invitingUser.setStatus(UserStatus.IN_GAME);

        this.invitedUser = new User();
        invitedUser.setId(2L);
        invitedUser.setUsername("anotherUser");
        invitedUser.setPassword("testPassword");
        invitedUser.setPoints(2L);
        invitedUser.setEmail("anotherUser@email.com");
        invitedUser.setProfilePicture("anotherUser");
        invitedUser.setToken("2");
        invitedUser.setStatus(UserStatus.IN_GAME);
    }

    @Test
    void addAnswer() {
        UserAnswerTuple invitingUserAnswerTuple = new UserAnswerTuple(invitingUser.getId(), question.getId(), question.getIncorrectAnswers()[1], 200L);
        UserAnswerTuple invitedUserAnswerTuple = new UserAnswerTuple(invitedUser.getId(), question.getId(), question.getCorrectAnswer(), 300L);

        question.addAnswer(invitingUserAnswerTuple);
        question.addAnswer(invitedUserAnswerTuple);

        Map<Long, UserAnswerTuple> testResults = question.getResults();

        assertEquals(invitingUserAnswerTuple, testResults.get(invitingUser.getId()));
        assertEquals(invitedUserAnswerTuple, testResults.get(invitedUser.getId()));
    }

    @Test
    void lastCorrect_singleAnswer() {
        UserAnswerTuple invitingUserAnswerTuple = new UserAnswerTuple(invitingUser.getId(), question.getId(), question.getIncorrectAnswers()[1], 200L);

        assertThrows(ResponseStatusException.class, () -> {
            question.lastCorrect(invitingUser.getId());
        });

        question.addAnswer(invitingUserAnswerTuple);

        assertEquals(invitingUserAnswerTuple.getAnswer().equals(question.getCorrectAnswer()), question.lastCorrect(invitingUser.getId()).get("boolean"));
    }

    @Test
    void lastCorrect_multipleAnswers() {
        UserAnswerTuple invitingUserAnswerTuple = new UserAnswerTuple(invitingUser.getId(), question.getId(), question.getIncorrectAnswers()[1], 200L);
        UserAnswerTuple invitedUserAnswerTuple = new UserAnswerTuple(invitedUser.getId(), question.getId(), question.getCorrectAnswer(), 300L);

        question.addAnswer(invitingUserAnswerTuple);

        assertEquals(invitingUserAnswerTuple.getAnswer().equals(question.getCorrectAnswer()), question.lastCorrect(invitingUser.getId()).get("boolean"));
        assertThrows(ResponseStatusException.class, () -> {
            question.lastCorrect(invitedUser.getId());
        });
        question.addAnswer(invitedUserAnswerTuple);

        assertEquals(invitingUserAnswerTuple.getAnswer().equals(question.getCorrectAnswer()), question.lastCorrect(invitingUser.getId()).get("boolean"));
        assertEquals(invitedUserAnswerTuple.getAnswer().equals(question.getCorrectAnswer()), question.lastCorrect(invitedUser.getId()).get("boolean"));
    }

    @Test
    void getPoints() {
        UserAnswerTuple invitingUserAnswerTuple = new UserAnswerTuple(invitingUser.getId(), question.getId(), question.getIncorrectAnswers()[1], 200L);
        UserAnswerTuple invitedUserAnswerTuple = new UserAnswerTuple(invitedUser.getId(), question.getId(), question.getCorrectAnswer(), 300L);

        question.addAnswer(invitingUserAnswerTuple);
        question.addAnswer(invitedUserAnswerTuple);

        assertEquals(0L, question.getPoints(invitingUser.getId()));
        assertEquals((500L - (0.5 * invitedUserAnswerTuple.getAnsweredTime())), question.getPoints(invitedUser.getId()));
    }
}