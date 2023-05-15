package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.Category;
import ch.uzh.ifi.hase.soprafs23.converter.StringListConverter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "QUESTION")
public class Question implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long questionId;
  @Column(nullable = false)
  private long gameId;
  @Column
  private Category category;
  @Column(nullable = false)
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
  private String questionString;
  @Column(nullable = false)
  private Date creationTime;

  public boolean timeRunUp() {
        return (new Date().getTime() - this.creationTime.getTime())/1000 >= 30;
    }
}
