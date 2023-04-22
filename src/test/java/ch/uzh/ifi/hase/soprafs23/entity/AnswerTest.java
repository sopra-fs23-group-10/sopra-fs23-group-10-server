/*package ch.uzh.ifi.hase.soprafs23.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnswerTest {

    private Answer answer;

    @BeforeEach
    void setup() {
        answer = new Answer(1L, "questionId", "someAnswer", 2044L);
    }

    @Test
    void testToString() {
        assertEquals("ResultTuple(userId=1, answeredTime=2044)", answer.toString());
    }

    @Test
    void getUserId_thenSet() {
        assertEquals(1L, answer.getUserId());
        answer.setUserId(54L);
        assertEquals(54L, answer.getUserId());
    }

    @Test
    void getQuestionId_thenSet() {
        assertEquals("questionId", answer.getQuestionId());
        answer.setQuestionId("aDifferentQuestionID");
        assertEquals("aDifferentQuestionID", answer.getQuestionId());
    }

    @Test
    void getAnswer_thenSet() {
        assertEquals("someAnswer", answer.getAnswer());
        answer.setAnswer("differentAnswer");
        assertEquals("differentAnswer", answer.getAnswer());
    }

    @Test
    void getAnsweredTime_thenSet() {
        assertEquals(2044L, answer.getAnsweredTime());
        answer.setAnsweredTime(541L);
        assertEquals(541L, answer.getAnsweredTime());
    }
}*/