package ch.uzh.ifi.hase.soprafs23.entity;

import org.springframework.data.util.Pair;

import java.util.ArrayList;

public class UserResultTuple {
    private long gameId;
    private long invitingPlayerId;
    private long invitingPlayerResult;
    private long invitedPlayerId;
    private long invitedPlayerResult;

    private ArrayList<Pair<Integer, Long>> invitedPlayerHistory = new ArrayList<>();
    private ArrayList<Pair<Integer, Long>> invitingPlayerHistory = new ArrayList<>();


    public UserResultTuple(long gameId, long invitingPlayerId, long invitedPlayerId) {
        this.gameId = gameId;
        this.invitingPlayerId = invitingPlayerId;
        this.invitingPlayerResult = 0L;
        this.invitedPlayerId = invitedPlayerId;
        this.invitedPlayerResult = 0L;
    }

    public UserResultTuple() {
    }

    public long getGameId() {
        return this.gameId;
    }
    public long getInvitingPlayerId() {
        return this.invitingPlayerId;
    }
    public long getInvitingPlayerResult() {
        return this.invitingPlayerResult;
    }
    public long getInvitedPlayerId() {
        return this.invitedPlayerId;
    }
    public long getInvitedPlayerResult() {
        return this.invitedPlayerResult;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }
    public void setInvitingPlayerId(long invitingPlayerId) {
        this.invitingPlayerId = invitingPlayerId;
    }
    public void setInvitingPlayerResult(long invitingPlayerResult) {
        this.invitingPlayerResult = invitingPlayerResult;
    }
    public void setInvitedPlayerId(long invitedPlayerId) {
        this.invitedPlayerId = invitedPlayerId;
    }
    public void setInvitedPlayerResult(long invitedPlayerResult) {
        this.invitedPlayerResult = invitedPlayerResult;
    }
}
