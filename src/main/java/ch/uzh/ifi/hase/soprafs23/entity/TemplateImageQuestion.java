package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.converter.StringListConverter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "IMAGE_QUESTION")
public class TemplateImageQuestion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long templateImageQuestionId;
    @Column()
    private String apiId;
    @Column(nullable = false)
    private String correctAnswer;
    @Column(nullable = false)
    @Convert(converter = StringListConverter.class)
    private List<String> incorrectAnswers;
    @Column(nullable = false)
    @Convert(converter = StringListConverter.class)
    private List<String> allAnswers;
    @Column(nullable = false)
    private String question;
}
