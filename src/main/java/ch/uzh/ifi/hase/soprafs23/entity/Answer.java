package ch.uzh.ifi.hase.soprafs23.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Setter
@Entity
@Table(name = "ANSWER")
@Getter
public class Answer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private long questionId;
    @Column(nullable = false)
    private long userId;
    @Column(nullable = false)
    private String answer;
    @Column(nullable = false)
    private long answeredTime;
}
