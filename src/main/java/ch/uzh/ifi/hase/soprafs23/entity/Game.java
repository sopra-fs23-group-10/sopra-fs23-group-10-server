package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.ModeType;
import ch.uzh.ifi.hase.soprafs23.constant.QuizType;

import java.util.*;


public class Game {
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
        this.lastChange = seconds > 20 ? this.lastChange : new Date();
        return seconds > 20;
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

    public UserResultTuple getResults() {
        UserResultTuple userResultTuple = new UserResultTuple(this.id, invitedUserId,invitingUserId);
        for (Question question : questions) {
            userResultTuple.setInvitedPlayerResult(question.getPoints(this.invitedUserId));
            userResultTuple.setInvitingPlayerResult(question.getPoints(this.invitingUserId));
        }
        return userResultTuple;
    }

    public Map<Long,Long> getPoints(long userId) {
        long points = 0L;
        for (Question question : questions) {
            points += question.getPoints(userId);
        }
        return Collections.singletonMap(userId,points);
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
            Question question = questions.peek();
            question.addAnswer(userAnswerTuple);
        }

    }

    public Boolean completelyAnswered(){
        return questions.peek().completelyAnswered();
    }

    public Long changeCurrentPlayer() {
        currentPlayer = (currentPlayer == invitingUserId) ? invitedUserId : invitingUserId;
        return currentPlayer;
    }
}
