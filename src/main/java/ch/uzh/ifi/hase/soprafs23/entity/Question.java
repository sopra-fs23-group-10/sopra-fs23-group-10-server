package ch.uzh.ifi.hase.soprafs23.entity;
import ch.uzh.ifi.hase.soprafs23.constant.Category;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import lombok.*;
import org.json.JSONException;
import org.json.JSONObject;

@Getter
@Setter
@ToString
public class Question {
    private final Category category;
    private final long questionId;
    private final String correctAnswer;
    private final String question;
    private ArrayList<ResultTuple> results = new ArrayList<>();

    public Question(Category category, long questionId, String correctAnswer, String question){
        this.category = category;
        this.questionId = questionId;
        this.question = question;
        this.correctAnswer =  correctAnswer;
    }

    public void answerQuestion(String answer, long userId, float answeredTime) throws Exception {
        for (ResultTuple res:results){
            if (res.getUserId() == userId){
                throw new Exception("User already answered");
            }
        }
        Boolean correct = answer == correctAnswer;
        results.add(new ResultTuple(userId, correct, answeredTime));
    }

    public long getPoints(User user){
        long sum = 0;
        for(ResultTuple res:results){
            if(res.getUserId() == user.getId()){
                sum += res.getPoints();
            }
        }
        return sum;
    }
}
