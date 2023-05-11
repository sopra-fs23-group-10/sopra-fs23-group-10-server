package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.ModeType;
import ch.uzh.ifi.hase.soprafs23.constant.QuizType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Setter
@Getter
@Entity
@Table(name = "GAME")
public class Game implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
}
