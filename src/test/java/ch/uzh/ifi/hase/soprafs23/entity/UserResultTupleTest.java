package ch.uzh.ifi.hase.soprafs23.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserResultTupleTest {

    private UserResultTuple userResultTuple;

    @BeforeEach
    void setup() {
        userResultTuple = new UserResultTuple(1L,  1L, 9L);
    }

    @Test
    void getGameId_thenSet_success() {
        assertEquals(1L, userResultTuple.getGameId());
        userResultTuple.setGameId(14L);
        assertEquals(14L, userResultTuple.getGameId());
    }

    @Test
    void getInvitingPlayerId_thenSet_success() {
        assertEquals(1L, userResultTuple.getInvitingPlayerId());
        userResultTuple.setInvitingPlayerId(17L);
        assertEquals(17L, userResultTuple.getInvitingPlayerId());
    }

    @Test
    void getInvitingPlayerResult_thenSet_success() {
        assertEquals(0L, userResultTuple.getInvitingPlayerResult());
        userResultTuple.setInvitingPlayerResult(1400L);
        assertEquals(1400L, userResultTuple.getInvitingPlayerResult());
    }

    @Test
    void setInvitedPlayerResult_thenSet_success() {
        assertEquals(0L, userResultTuple.getInvitedPlayerResult());
        userResultTuple.setInvitedPlayerResult(2000L);
        assertEquals(2000L, userResultTuple.getInvitedPlayerResult());
        userResultTuple.setInvitedPlayerResult(userResultTuple.getInvitedPlayerResult() + 1000L);
        assertEquals(3000L, userResultTuple.getInvitedPlayerResult());
    }

    @Test
    void getInvitedPlayerId_thenSet_success() {
        assertEquals(9L, userResultTuple.getInvitedPlayerId());
        userResultTuple.setInvitedPlayerId(24L);
        assertEquals(24L, userResultTuple.getInvitedPlayerId());
    }
}