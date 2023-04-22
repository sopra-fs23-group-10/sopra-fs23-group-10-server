/*package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.Category;
import ch.uzh.ifi.hase.soprafs23.constant.ModeType;
import ch.uzh.ifi.hase.soprafs23.constant.QuizType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    private Game game;

    private Question question;

    private Answer invitingAnswer;
    private Answer invitedAnswer;

    @BeforeEach
    public void setup() {
        game = new Game(0L, 1L, 2L, QuizType.TEXT, ModeType.DUEL);

        question = new Question();
        String[] incorrectAnswers = {"Neil Young", "Eric Clapton", "Elton John"};
        String[] allAnswers = {"Neil Young", "Bob Dylan", "Eric Clapton", "Elton John"};

        question.setCategory(Category.MUSIC);
        question.setId("622a1c357cc59eab6f94ff56");
        question.setCorrectAnswer("Bob Dylan");
        question.setIncorrectAnswers(incorrectAnswers);
        question.setAllAnswers(allAnswers);
        question.setQuestion("Which musician has famously performed over 3,000 shows in their 'Never Ending Tour'?");

        game.addQuestion(question);

        invitingAnswer = new Answer(game.getInvitingUserId(), question.getId(), question.getIncorrectAnswers()[1], 200L);
        invitedAnswer = new Answer(game.getInvitedUserId(), question.getId(), question.getCorrectAnswer(), 300L);
    }

    @Test
    void getResultsOfBoth_singleQuestion_success() {
        assertNotNull(game.getQuestions().peekFirst());
        Map<Long, Answer> results = game.getQuestions().peekFirst().getResults();
        assertNull(results.get(invitingAnswer.getUserId()));
        assertNull(results.get(invitedAnswer.getUserId()));

        results.put(invitingAnswer.getUserId(), invitingAnswer);
        results.put(invitedAnswer.getUserId(), invitedAnswer);

        UserResultTuple userResultTuple = game.getPointsOfBoth();

        assertEquals(game.getId(), userResultTuple.getGameId());
        assertEquals(game.getInvitingUserId(), userResultTuple.getInvitingPlayerId());
        assertEquals(0L, userResultTuple.getInvitingPlayerResult());
        assertEquals(game.getInvitedUserId(), userResultTuple.getInvitedPlayerId());
        assertEquals(500L - (0.5 * invitedAnswer.getAnsweredTime()), userResultTuple.getInvitedPlayerResult());
    }

    @Test
    void getResults_multipleQuestions_success() {
        assertNotNull(game.getQuestions().peekFirst());
        assertNull(game.getQuestions().peekFirst().getResults().get(invitingAnswer.getUserId()));
        assertNull(game.getQuestions().peekFirst().getResults().get(invitedAnswer.getUserId()));

        game.addAnswer(invitingAnswer);
        game.addAnswer(invitedAnswer);

        UserResultTuple userResultTuple = game.getPointsOfBoth();

        assertEquals(game.getId(), userResultTuple.getGameId());
        assertEquals(invitingAnswer.getUserId(), userResultTuple.getInvitingPlayerId());
        assertEquals(invitedAnswer.getUserId(), userResultTuple.getInvitedPlayerId());
        assertEquals(0L, userResultTuple.getInvitingPlayerResult());
        assertEquals((500L - (0.5 * invitedAnswer.getAnsweredTime())), userResultTuple.getInvitedPlayerResult());

        Question anotherQuestion = new Question();
        String[] incorrectAnswers = {"Paul McCartney", "Elton John", "Patti Smith"};
        String[] allAnswers = {"Paul McCartney", "Elton John", "Prince", "Patti Smith"};

        anotherQuestion.setCategory(Category.MUSIC);
        anotherQuestion.setId("622a1c357cc59eab6f94fd89");
        anotherQuestion.setCorrectAnswer("Prince");
        anotherQuestion.setIncorrectAnswers(incorrectAnswers);
        anotherQuestion.setAllAnswers(allAnswers);
        anotherQuestion.setQuestion("Who Wrote The Song \"Manic Monday\" For The Bangles\"");

        Answer invitingSecondAnswer = new Answer(invitingAnswer.getUserId(), anotherQuestion.getId(), anotherQuestion.getCorrectAnswer(), 200L);
        Answer invitedSecondAnswer = new Answer(invitedAnswer.getUserId(), anotherQuestion.getId(), anotherQuestion.getCorrectAnswer(), 250L);

        game.addQuestion(anotherQuestion);

        assertNotNull(game.getQuestions().peekFirst());
        assertNull(game.getQuestions().peekFirst().getResults().get(invitingAnswer.getUserId()));
        assertNull(game.getQuestions().peekFirst().getResults().get(invitedAnswer.getUserId()));

        game.addAnswer(invitingSecondAnswer);
        game.addAnswer(invitedSecondAnswer);

        userResultTuple = game.getPointsOfBoth();

        assertEquals(game.getId(), userResultTuple.getGameId());
        assertEquals(invitingAnswer.getUserId(), userResultTuple.getInvitingPlayerId());
        assertEquals(invitedAnswer.getUserId(), userResultTuple.getInvitedPlayerId());
        assertEquals((0L + (500L - (0.5 * invitingSecondAnswer.getAnsweredTime()))), userResultTuple.getInvitingPlayerResult());
        assertEquals(((500L - (0.5 * invitedAnswer.getAnsweredTime())) + (500L - (0.5 * invitedSecondAnswer.getAnsweredTime()))), userResultTuple.getInvitedPlayerResult());
    }

    @Test
    void getPoints_success() {
        assertNotNull(game.getQuestions().peekFirst());
        Map<Long, Answer> results = game.getQuestions().peekFirst().getResults();
        assertNull(results.get(invitingAnswer.getUserId()));
        assertNull(results.get(invitedAnswer.getUserId()));

        results.put(invitingAnswer.getUserId(), invitingAnswer);
        results.put(invitedAnswer.getUserId(), invitedAnswer);

        assertEquals(0L, game.getPoints(invitingAnswer.getUserId()));
        assertEquals(500L - (0.5 * invitedAnswer.getAnsweredTime()), game.getPoints(invitedAnswer.getUserId()));
    }

    @Test
    void addAnswer_success() {
        assertNotNull(game.getQuestions().peekFirst());
        assertNull(game.getQuestions().peekFirst().getResults().get(invitedAnswer.getUserId()));

        game.addAnswer(invitedAnswer);

        assertNotNull(game.getQuestions().peekFirst());
        assertEquals(invitedAnswer.getAnsweredTime(), game.getQuestions().peekFirst().getResults().get(invitedAnswer.getUserId()).getAnsweredTime());
    }

    @Test
    void addAnswer_alreadyAnswered_throwsException() {
        assertNotNull(game.getQuestions().peekFirst());
        assertNull(game.getQuestions().peekFirst().getResults().get(invitedAnswer.getUserId()));

        game.addAnswer(invitedAnswer);

        assertThrows(ResponseStatusException.class, () -> {
            game.addAnswer(invitedAnswer);
        });
    }

    @Test
    void completelyAnswered_success() {
        assertNotNull(game.getQuestions().peekFirst());
        assertNull(game.getQuestions().peekFirst().getResults().get(invitingAnswer.getUserId()));
        assertNull(game.getQuestions().peekFirst().getResults().get(invitedAnswer.getUserId()));

        game.addAnswer(invitingAnswer);
        game.addAnswer(invitedAnswer);

        assertTrue(game.completelyAnswered());
    }

    @Test
    void completelyAnswered_returnsFalse() {
        assertNotNull(game.getQuestions().peekFirst());
        assertNull(game.getQuestions().peekFirst().getResults().get(invitingAnswer.getUserId()));
        assertNull(game.getQuestions().peekFirst().getResults().get(invitedAnswer.getUserId()));

        game.addAnswer(invitingAnswer);

        assertFalse(game.completelyAnswered());
    }

    @Test
    void changeCurrentPlayer() {
        long startPlayer = game.getCurrentPlayer();
        game.changeCurrentPlayer();
        assertNotEquals(startPlayer, game.getCurrentPlayer());
    }
}*/