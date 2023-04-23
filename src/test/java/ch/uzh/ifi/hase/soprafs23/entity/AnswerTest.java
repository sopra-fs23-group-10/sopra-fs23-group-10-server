package ch.uzh.ifi.hase.soprafs23.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnswerTest {

    private Answer answer;

    @BeforeEach
    void setup() {
        answer = new Answer();
        answer.setAnswer("answer");
        answer.setAnsweredTime(0L);
        answer.setId(1L);
        answer.setQuestionId(2L);
    }

    @Test
    void getUserId_thenSet() {
        assertEquals(0L, answer.getUserId());
        answer.setUserId(54L);
        assertEquals(54L, answer.getUserId());
    }

    @Test
    void getQuestionId_thenSet() {
        assertEquals(2, answer.getQuestionId());
        answer.setQuestionId(0);
        assertEquals(0, answer.getQuestionId());
    }

    @Test
    void getAnswer_thenSet() {
        assertEquals("answer", answer.getAnswer());
        answer.setAnswer("differentAnswer");
        assertEquals("differentAnswer", answer.getAnswer());
    }

    @Test
    void getAnsweredTime_thenSet() {
        assertEquals(0L, answer.getAnsweredTime());
        answer.setAnsweredTime(541L);
        assertEquals(541L, answer.getAnsweredTime());
    }
}