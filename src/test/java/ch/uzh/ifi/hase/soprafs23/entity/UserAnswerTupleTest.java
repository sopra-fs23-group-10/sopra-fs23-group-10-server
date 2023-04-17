package ch.uzh.ifi.hase.soprafs23.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserAnswerTupleTest {

    private UserAnswerTuple userAnswerTuple;

    @BeforeEach
    void setup() {
        userAnswerTuple = new UserAnswerTuple(1L, "questionId", "someAnswer", 2044L);
    }

    @Test
    void testToString() {
        assertEquals("ResultTuple(userId=1, answeredTime=2044)", userAnswerTuple.toString());
    }

    @Test
    void getUserId_thenSet() {
        assertEquals(1L, userAnswerTuple.getUserId());
        userAnswerTuple.setUserId(54L);
        assertEquals(54L, userAnswerTuple.getUserId());
    }

    @Test
    void getQuestionId_thenSet() {
        assertEquals("questionId", userAnswerTuple.getQuestionId());
        userAnswerTuple.setQuestionId("aDifferentQuestionID");
        assertEquals("aDifferentQuestionID", userAnswerTuple.getQuestionId());
    }

    @Test
    void getAnswer_thenSet() {
        assertEquals("someAnswer", userAnswerTuple.getAnswer());
        userAnswerTuple.setAnswer("differentAnswer");
        assertEquals("differentAnswer", userAnswerTuple.getAnswer());
    }

    @Test
    void getAnsweredTime_thenSet() {
        assertEquals(2044L, userAnswerTuple.getAnsweredTime());
        userAnswerTuple.setAnsweredTime(541L);
        assertEquals(541L, userAnswerTuple.getAnsweredTime());
    }
}