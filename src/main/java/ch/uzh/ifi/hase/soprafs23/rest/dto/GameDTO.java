package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.constant.ModeType;
import ch.uzh.ifi.hase.soprafs23.constant.QuizType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameDTO {
    private long gameId;
    private long invitedUserId;
    private long invitingUserId;
    private ModeType modeType;
    private QuizType quizType;
}
