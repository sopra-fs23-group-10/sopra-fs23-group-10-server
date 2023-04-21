package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.ModeType;
import ch.uzh.ifi.hase.soprafs23.constant.QuizType;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name = "GAME")
public class Game implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private long gameId;
    private long invitingUserId;
    private long invitedUserId;
    private QuizType quizType;
    private ModeType modeType;
    private long currentPlayer;
    private Date lastChange;

    public long getGameId() {
        return this.gameId;
    }
    public long getInvitingUserId() {
        return this.invitingUserId;
    }
    public long getInvitedUserId() {
        return this.invitedUserId;
    }
    public QuizType getQuizType() {
        return this.quizType;
    }
    public ModeType getModeType() {
        return this.modeType;
    }
    public long getCurrentPlayer() {
        return this.currentPlayer;
    }
    public Date getLastChange() {
        return this.lastChange;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }
    public void setInvitingUserId(long invitingUserId) {
        this.invitingUserId = invitingUserId;
    }
    public void setInvitedUserId(long invitedUserId) {
        this.invitedUserId = invitedUserId;
    }
    public void setQuizType(QuizType quizType) {
        this.quizType = quizType;
    }
    public void setModeType(ModeType modeType) {
        this.modeType = modeType;
    }
    public void setCurrentPlayer(long currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
    public void setLastChange(Date lastChange) {
        this.lastChange = lastChange;
    }
}
