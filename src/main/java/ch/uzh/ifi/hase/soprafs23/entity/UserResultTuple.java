package ch.uzh.ifi.hase.soprafs23.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class UserResultTuple implements Serializable {
    private long gameId;
    private long invitingPlayerId;
    private long invitingPlayerResult;
    private long invitedPlayerId;
    private long invitedPlayerResult;

    public UserResultTuple(long gameId, long invitingPlayerId, long invitedPlayerId) {
        this.gameId = gameId;
        this.invitingPlayerId = invitingPlayerId;
        this.invitingPlayerResult = 0L;
        this.invitedPlayerId = invitedPlayerId;
        this.invitedPlayerResult = 0L;
    }
}