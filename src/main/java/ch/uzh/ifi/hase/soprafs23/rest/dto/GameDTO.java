package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.constant.ModeType;
import ch.uzh.ifi.hase.soprafs23.constant.QuizType;
import ch.uzh.ifi.hase.soprafs23.entity.User;

public class GameDTO {
    private long id;
    private long invitedUserId;
    private long invitingUserId;
    private ModeType modeType;
    private QuizType quizType;

    public long getId(){
        return this.id;
    }

    public long getInvitingUserId() {
        return this.invitingUserId;
    }

    public long getInvitedUserId() {
        return this.invitedUserId;
    }

    public ModeType getModeType() {
        return this.modeType;
    }

    public QuizType getQuizType() {
        return this.quizType;
    }
    public void setId(long id){this.id = id;}

    public void setInvitedUserId(long invitedUserId) {
        this.invitedUserId = invitedUserId;
    }

    public void setInvitingUserId(long invitingUserId) {
        this.invitingUserId = invitingUserId;
    }

    public void setModeType(ModeType modeType) {
        this.modeType = modeType;
    }

    public void setQuizType(QuizType quizType) {
        this.quizType = quizType;
    }
}
