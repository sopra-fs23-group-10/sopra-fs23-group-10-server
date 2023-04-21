package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.ModeType;
import ch.uzh.ifi.hase.soprafs23.constant.QuizType;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserResultTupleDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.Serializable;
import java.util.*;


public class Game implements Serializable {
    private long id;
    private long invitingUserId;
    private long invitedUserId;
    private final Deque<Question> questions = new ArrayDeque<>();
    private QuizType quizType;
    private ModeType modeType;
    private long currentPlayer;
    private Date lastChange;

    public Game(long gameId, long invitingUserId, long invitedUserId, QuizType quizType, ModeType modeType) {
        this.id = gameId;
        this.invitingUserId = invitingUserId;
        this.invitedUserId = invitedUserId;
        this.quizType = quizType;
        this.modeType = modeType;
        this.currentPlayer = invitedUserId;
        this.lastChange = new Date();
    }

    public Game() {}

    private boolean timeRunUp(){
        long seconds = (new Date().getTime()-this.lastChange.getTime())/1000;
        this.lastChange = seconds > 20000 ? this.lastChange : new Date();
        return seconds > 20000;
    }

    public void addQuestion(Question question){
        questions.addFirst(question);
    }

    public long getId() {
        return this.id;
    }

    public long getInvitingUserId() {
        return this.invitingUserId;
    }

    public long getInvitedUserId() {return this.invitedUserId;}

    public Deque<Question> getQuestions() {
        return this.questions;
    }

    public QuizType getQuizType() {
        return this.quizType;
    }

    public ModeType getModeType() {
        return this.modeType;
    }

    public long getCurrentPlayer() {
        return currentPlayer;
    }

    public void setId(long id) {this.id = id;}

    public void setInvitingUserId(long invitingUserId) {
        this.invitingUserId = invitingUserId;
    }

    public void setInvitedUserId(long invitedUserId) {this.invitedUserId = invitedUserId;}

    public void setQuizType(QuizType quizType) {
        this.quizType = quizType;
    }

    public void setModeType(ModeType modeType) {
        this.modeType = modeType;
    }

    public String toString() {
        return "Game(gameId=" + this.getId() + ", invitingUser=" + this.getInvitingUserId() + ", invitedUser=" + this.getInvitedUserId() + ", questions=" + this.getQuestions() + ", quizType=" + this.getQuizType() + ", modeType=" + this.getModeType() + ")";
    }

    public List<UserResultTupleDTO> getResults() {
        List<UserResultTupleDTO> userResultTupleDTOList= new ArrayList<>();

        for (Question question : questions) {
            UserResultTuple userResultTuple = new UserResultTuple(this.id, invitingUserId, invitedUserId);
            userResultTuple.setInvitedPlayerResult(question.getPoints(this.invitedUserId));
            userResultTuple.setInvitingPlayerResult(question.getPoints(this.invitingUserId));

            UserResultTupleDTO userResultTupleDTO = DTOMapper.INSTANCE.convertUserResultTupleEntitytoDTO(userResultTuple);
            userResultTupleDTOList.add(userResultTupleDTO);
        }
        return userResultTupleDTOList;
    }

    public long getPoints(long userId) {
        long points = 0L;
        for (Question question : questions) {
            points += question.getPoints(userId);
        }
        return points;
    }

    public UserResultTuple getPointsOfBoth() {
        UserResultTuple userResultTuple = new UserResultTuple(this.id, invitingUserId, invitedUserId);
        for (Question question : questions) {
            userResultTuple.setInvitedPlayerResult(userResultTuple.getInvitedPlayerResult() + question.getPoints(this.invitedUserId));
            userResultTuple.setInvitingPlayerResult(userResultTuple.getInvitingPlayerResult() + question.getPoints(this.invitingUserId));
        }
        return userResultTuple;
    }

    public void addAnswer(UserAnswerTuple userAnswerTuple) {
        if(timeRunUp()){
            Question question = questions.peek();
            question.addAnswer(
                    new UserAnswerTuple(userAnswerTuple.getUserId(),
                            userAnswerTuple.getQuestionId(),
                            "WrongAnswerAnyway",
                            1000L
                    )
            );
        }
        else {
            Question question = questions.peekFirst();
            question.addAnswer(userAnswerTuple);
        }

    }

    public Boolean completelyAnswered() {
        return questions.peekFirst().completelyAnswered();
    }

    public void changeCurrentPlayer() {
        currentPlayer = (currentPlayer == invitingUserId) ? invitedUserId : invitingUserId;
    }
}
