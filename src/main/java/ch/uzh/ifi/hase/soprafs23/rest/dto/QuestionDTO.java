package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.constant.Category;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuestionDTO {
    private long gameId;
    private long questionId;
    private Category category;
    private String apiId;
    private String correctAnswer;
    private List<String> incorrectAnswers;
    private List<String> allAnswers;
    private String question;
}
