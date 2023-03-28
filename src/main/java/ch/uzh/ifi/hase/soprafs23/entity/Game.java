package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.ModeType;
import ch.uzh.ifi.hase.soprafs23.constant.QuizType;
import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Stack;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Game {
    private final long gameId;
    private final ArrayList<User> users;
    private Stack<Question> questions;
    private final QuizType quizType;
    private final ModeType modeType;

    
}
