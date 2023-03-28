package ch.uzh.ifi.hase.soprafs23.entity;
import lombok.*;

@Getter
@Setter
@ToString
public class ResultTuple {
    private final long userId;
    private final Boolean answeredCorrect;
    private final float answeredTime;
    private float points;

    public ResultTuple(long userId, Boolean answeredCorrect, float answeredTime){
        this.userId = userId;
        this.answeredCorrect = answeredCorrect;
        this.answeredTime = answeredTime;
    }
}
