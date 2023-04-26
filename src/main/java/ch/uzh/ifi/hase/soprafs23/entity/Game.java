package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.ModeType;
import ch.uzh.ifi.hase.soprafs23.constant.QuizType;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "GAME")
public class Game implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private long gameId;
    @Column(nullable = false)
    private long invitingUserId;
    @Column(nullable = false)
    private long invitedUserId;
    @Column(nullable = false)
    private QuizType quizType;
    @Column(nullable = false)
    private ModeType modeType;
    @Column(nullable = false)
    private long currentPlayer;


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
}
