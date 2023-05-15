package ch.uzh.ifi.hase.soprafs23.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnswerTest {

    private Answer answer;

    @BeforeEach
    void setup() {
        answer = new Answer();
        answer.setAnswerString("answer");
        answer.setAnsweredTime(0L);
        answer.setId(1L);
        answer.setQuestionId(2L);
    }

    @Test
    void getAndSetUserId_thenSet() {
        assertEquals(0L, answer.getUserId());
        answer.setUserId(54L);
        assertEquals(54L, answer.getUserId());
    }

    @Test
    void getAndSetQuestionId_thenSet() {
        assertEquals(2, answer.getQuestionId());
        answer.setQuestionId(0);
        assertEquals(0, answer.getQuestionId());
    }

    @Test
    void getAndSetAnswer_thenSet() {
        assertEquals("answer", answer.getAnswerString());
        answer.setAnswerString("differentAnswer");
        assertEquals("differentAnswer", answer.getAnswerString());
    }

    @Test
    void getAndSetAnsweredTime_thenSet() {
        assertEquals(0L, answer.getAnsweredTime());
        answer.setAnsweredTime(541L);
        assertEquals(541L, answer.getAnsweredTime());
    }
}