package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.constant.ModeType;
import ch.uzh.ifi.hase.soprafs23.constant.QuizType;

public class GameDTO {

    private long invitedUserId;
    private ModeType modeType;
    private QuizType quizType;

    public long getInvitedUserId() {
        return this.invitedUserId;
    }

    public ModeType getModeType() {
        return this.modeType;
    }

    public QuizType getQuizType() {
        return this.quizType;
    }

    public void setInvitedUserId(long invitedUserId) {
        this.invitedUserId = invitedUserId;
    }

    public void setModeType(ModeType modeType) {
        this.modeType = modeType;
    }

    public void setQuizType(QuizType quizType) {
        this.quizType = quizType;
    }
}
