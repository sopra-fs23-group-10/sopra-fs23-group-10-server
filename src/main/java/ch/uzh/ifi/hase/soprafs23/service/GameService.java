package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.ModeType;
import ch.uzh.ifi.hase.soprafs23.constant.QuizType;
import ch.uzh.ifi.hase.soprafs23.entity.Answer;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Question;
import ch.uzh.ifi.hase.soprafs23.entity.UserResultTuple;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserResultTupleDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class GameService {
    private final Logger log = LoggerFactory.getLogger(GameService.class);
    private final GameRepository gameRepository;
    private final QuestionService questionService;

    @Autowired
    public GameService(@Qualifier("gameRepository") GameRepository gameRepository, QuestionService questionService) {
        this.gameRepository = gameRepository;
        this.questionService = questionService;
    }


    public Game createGame(Long invitingUserId, Long invitedUserId, QuizType quizType, ModeType modeType) {
        Game game = new Game();
        game.setInvitingUserId(invitingUserId);
        game.setInvitedUserId(invitedUserId);
        game.setQuizType(quizType);
        game.setModeType(modeType);
        game.setCurrentPlayer(invitedUserId);
        game.setLastChange(new Date());

        game = gameRepository.save(game);

        return game;
    }

    public Game searchGameById(Long gameId) {
        Game game = gameRepository.findGameByGameId(gameId);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game with specified ID cannot be found.");
        }
        return game;
    }

    //TODO: Delete related Questions and Answers too
    public void removeGame(Long gameId) {
        gameRepository.deleteGameByGameId(gameId);
    }

    public long getGameIdOfUser(Long userId) {
        Game invitingGame = gameRepository.findGameByInvitingUserId(userId);
        if (invitingGame != null) {
            return invitingGame.getInvitingUserId();
        }
        Game invitedGame = gameRepository.findGameByInvitedUserId(userId);
        if (invitedGame != null) {
            return invitedGame.getInvitedUserId();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Specified user is not in any game.");
    }
/*
    public String toString() {
        return "Game(gameId=" + this.getId() + ", invitingUser=" + this.getInvitingUserId() + ", invitedUser=" + this.getInvitedUserId() + ", questions=" + this.getQuestions() + ", quizType=" + this.getQuizType() + ", modeType=" + this.getModeType() + ")";
    }

 */

    public List<UserResultTupleDTO> getResults(long gameId) {
        Game game = searchGameById(gameId);

        List<UserResultTupleDTO> userResultTupleDTOList= new ArrayList<>();

        List<Question> questions = questionService.searchQuestionsByGameId(gameId);

        for (Question question : questions) {
            UserResultTuple userResultTuple = new UserResultTuple(game.getGameId(), game.getInvitingUserId(), game.getInvitedUserId());
            userResultTuple.setInvitedPlayerResult(questionService.getPoints(question.getQuestionId(), game.getInvitedUserId()));
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

    public synchronized void addAnswer(long gameId, Answer answer) {
        if (timeRunUp(gameId)) {
            Question question = questionService.searchQuestionByQuestionId(answer.getQuestionId());
            Answer placeholderAnswer = new Answer();
            placeholderAnswer.setUserId(answer.getUserId());
            placeholderAnswer.setQuestionId(answer.getQuestionId());
            placeholderAnswer.setAnswer("WrongAnswerAnywayBecauseYouTookTooLong");
            placeholderAnswer.setAnsweredTime(1000L);
            questionService.addAnswer(placeholderAnswer);
        }
        else {
            Question question = questionService.searchQuestionByQuestionId(answer.getQuestionId());
            questionService.addAnswer(answer);
        }

    }

    public synchronized Boolean completelyAnswered() {
        return questions.peekFirst().completelyAnswered();
    }

    public synchronized void changeCurrentPlayer(long gameId) {
        Game game = gameRepository.findGameByGameId(gameId);
        game.setCurrentPlayer((game.getCurrentPlayer() == game.getInvitingUserId()) ? game.getInvitedUserId() : game.getInvitingUserId());
    }

    private boolean timeRunUp(long gameId) {
        Game game = searchGameById(gameId);
        long seconds = (new Date().getTime() - game.getLastChange().getTime())/1000;
        game.setLastChange(seconds > 20000 ? game.getLastChange() : new Date());
        return seconds > 20000;
    }

    public synchronized void addQuestion(Question question){
        questions.addFirst(question);
    }
}
