package ch.uzh.ifi.hase.soprafs23.entity;

public class UserResultTuple {
    private long gameId;
    private long invitingPlayerId;
    private long invitingPlayerResult;
    private long invitedPlayerId;
    private long invitedPlayerResult;

    public UserResultTuple(long gameId, long invitingPlayerId, long invitingPlayerResult, long invitedPlayerId, long invitedPlayerResult) {
        this.gameId = gameId;
        this.invitingPlayerId = invitingPlayerId;
        this.invitingPlayerResult = invitingPlayerResult;
        this.invitedPlayerId = invitedPlayerId;
        this.invitedPlayerResult = invitedPlayerResult;
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
