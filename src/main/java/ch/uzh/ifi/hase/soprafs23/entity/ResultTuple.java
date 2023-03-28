package ch.uzh.ifi.hase.soprafs23.entity;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class ResultTuple {
    private final long userId;
    private final Boolean answeredCorrect;
    private final float answeredTime;
    private float points;
}
