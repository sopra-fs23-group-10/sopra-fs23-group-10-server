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

        this.invitedUser = new User();
        invitedUser.setId(2L);
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
    void getPoints() {
        UserAnswerTuple invitingUserAnswerTuple = new UserAnswerTuple(invitingUser.getId(), question.getId(), question.getIncorrectAnswers()[1], 200L);
        UserAnswerTuple invitedUserAnswerTuple = new UserAnswerTuple(invitedUser.getId(), question.getId(), question.getCorrectAnswer(), 300L);

        question.addAnswer(invitingUserAnswerTuple);
        question.addAnswer(invitedUserAnswerTuple);

        assertEquals(0L, question.getPoints(invitingUser.getId()));
        assertEquals((500L - (0.5 * invitedUserAnswerTuple.getAnsweredTime())), question.getPoints(invitedUser.getId()));
    }

    @Test
    void completelyAnswered_false() {
        assertFalse(question.completelyAnswered());
    }

    @Test
    void completelyAnswered_true() {
        UserAnswerTuple userAnswerTuple1 = new UserAnswerTuple(invitingUser.getId(), question.getId(), question.getIncorrectAnswers()[1], 200L);
        UserAnswerTuple userAnswerTuple2 = new UserAnswerTuple(invitedUser.getId(), question.getId(), question.getCorrectAnswer(), 300L);
        Map<Long, UserAnswerTuple> results = question.getResults();
        results.put(userAnswerTuple1.getUserId(), userAnswerTuple1);
        results.put(userAnswerTuple2.getUserId(), userAnswerTuple2);
        assertTrue(question.completelyAnswered());
    }
}