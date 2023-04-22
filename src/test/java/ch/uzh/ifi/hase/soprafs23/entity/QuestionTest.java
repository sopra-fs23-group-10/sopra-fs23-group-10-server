/*package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

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

        Map<Long, Answer> resultsMap = new HashMap<>();
        question.setResults(resultsMap);

        this.invitingUser = new User();
        invitingUser.setId(1L);

        this.invitedUser = new User();
        invitedUser.setId(2L);
    }

    @Test
    void addAnswer() {
        Answer invitingAnswer = new Answer(invitingUser.getId(), question.getId(), question.getIncorrectAnswers()[1], 200L);
        Answer invitedAnswer = new Answer(invitedUser.getId(), question.getId(), question.getCorrectAnswer(), 300L);

        question.addAnswer(invitingAnswer);
        question.addAnswer(invitedAnswer);

        Map<Long, Answer> testResults = question.getResults();

        assertEquals(invitingAnswer, testResults.get(invitingUser.getId()));
        assertEquals(invitedAnswer, testResults.get(invitedUser.getId()));
    }

    @Test
    void getPoints() {
        Answer invitingAnswer = new Answer(invitingUser.getId(), question.getId(), question.getIncorrectAnswers()[1], 200L);
        Answer invitedAnswer = new Answer(invitedUser.getId(), question.getId(), question.getCorrectAnswer(), 300L);

        question.addAnswer(invitingAnswer);
        question.addAnswer(invitedAnswer);

        assertEquals(0L, question.getPoints(invitingUser.getId()));
        assertEquals((500L - (0.5 * invitedAnswer.getAnsweredTime())), question.getPoints(invitedUser.getId()));
    }

    @Test
    void completelyAnswered_false() {
        assertFalse(question.completelyAnswered());
    }

    @Test
    void completelyAnswered_true() {
        Answer answer1 = new Answer(invitingUser.getId(), question.getId(), question.getIncorrectAnswers()[1], 200L);
        Answer answer2 = new Answer(invitedUser.getId(), question.getId(), question.getCorrectAnswer(), 300L);
        Map<Long, Answer> results = question.getResults();
        results.put(answer1.getUserId(), answer1);
        results.put(answer2.getUserId(), answer2);
        assertTrue(question.completelyAnswered());
    }
}*/