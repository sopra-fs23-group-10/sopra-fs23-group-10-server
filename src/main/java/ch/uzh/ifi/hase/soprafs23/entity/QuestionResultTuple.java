package ch.uzh.ifi.hase.soprafs23.entity;

public class QuestionResultTuple {
    private final long userId;
    private final Boolean answeredCorrect;
    private final float answeredTime;
    private float points;

    public QuestionResultTuple(long userId, Boolean answeredCorrect, float answeredTime){
        this.userId = userId;
        this.answeredCorrect = answeredCorrect;
        this.answeredTime = answeredTime;
    }

    public long getUserId() {
        return this.userId;
    }

    public Boolean getAnsweredCorrect() {
        return this.answeredCorrect;
    }

    public float getAnsweredTime() {
        return this.answeredTime;
    }

    public float getPoints() {
        return this.points;
    }

    public void setPoints(float points) {
        this.points = points;
    }

    public String toString() {
        return "ResultTuple(userId=" + this.getUserId() + ", answeredCorrect=" + this.getAnsweredCorrect() + ", answeredTime=" + this.getAnsweredTime() + ", points=" + this.getPoints() + ")";
    }
}
