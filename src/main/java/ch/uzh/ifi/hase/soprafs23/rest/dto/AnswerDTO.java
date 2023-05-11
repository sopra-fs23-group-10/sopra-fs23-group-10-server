package ch.uzh.ifi.hase.soprafs23.rest.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerDTO {
    private Long userId;
    private Long questionId;
    private String answerString;
    private Long answeredTime;
}
