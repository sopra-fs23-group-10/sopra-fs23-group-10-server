package ch.uzh.ifi.hase.soprafs23.entity;
import ch.uzh.ifi.hase.soprafs23.constant.Category;

import java.util.ArrayList;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Question {
    private final Category category;
    private final long id;
    private final String correctAnswer;
    private final String question;
    private ArrayList<ResultTuple> results;
}
