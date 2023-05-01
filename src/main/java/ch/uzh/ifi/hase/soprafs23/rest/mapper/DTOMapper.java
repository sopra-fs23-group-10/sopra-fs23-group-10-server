package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */
@Mapper
public interface DTOMapper {

    DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "email", target = "email")
    @Mapping(target = "points", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "token", ignore = true)
    @Mapping(target = "profilePicture", ignore = true)
    @Mapping(target = "backgroundMusic", ignore = true)
    User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "username", target = "username")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "points", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "token", ignore = true)
    @Mapping(source = "profilePicture", target = "profilePicture")
    @Mapping(target = "backgroundMusic", ignore = true)
    User convertUserPutDTOtoEntity(UserPutDTO userPostDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "points", target = "points")
    @Mapping(source = "profilePicture", target = "profilePicture")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "token", target = "token")
    UserGetDTO convertEntityToUserGetDTO(User user);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "points", target = "points")
    @Mapping(source = "profilePicture", target = "profilePicture")
    @Mapping(source = "status", target = "status")
    @Mapping(target = "token", ignore = true)
    UserGetDTO convertEntityToUserGetDTONoToken(User user);

    @Mapping(source = "gameId", target = "gameId")
    @Mapping(source = "invitingUserId", target = "invitingUserId")
    @Mapping(source = "invitedUserId", target = "invitedUserId")
    @Mapping(source = "quizType", target = "quizType")
    @Mapping(source = "modeType", target = "modeType")
    GameDTO convertGameEntityToPostDTO(Game game);

    @Mapping(source = "gameId", target = "gameId")
    @Mapping(source = "invitedUserId", target = "invitedUserId")
    @Mapping(source = "invitingUserId", target = "invitingUserId")
    @Mapping(source = "quizType", target = "quizType")
    @Mapping(source = "modeType", target = "modeType")
    @Mapping(target = "currentPlayer", ignore = true)
    Game convertGamePostDTOtoEntity(GameDTO gameDTO);

    @Mapping(source = "category", target = "category")
    @Mapping(source = "questionId", target = "questionId")
    @Mapping(source = "apiId", target = "apiId")
    @Mapping(source = "question", target = "question")
    @Mapping(source = "correctAnswer", target = "correctAnswer")
    @Mapping(source = "incorrectAnswers", target = "incorrectAnswers")
    @Mapping(target = "creationTime", ignore = true)
    Question convertQuestionDTOtoEntity(QuestionDTO questionDTO);

    @Mapping(source = "category", target = "category")
    @Mapping(source = "questionId", target = "questionId")
    @Mapping(source = "apiId", target = "apiId")
    @Mapping(source = "question", target = "question")
    @Mapping(source = "allAnswers", target = "allAnswers")
    @Mapping(target = "correctAnswer", ignore = true)
    @Mapping(target = "incorrectAnswers", ignore = true)
    QuestionDTO convertQuestionEntityToDTO(Question question);

    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "questionId", target = "questionId")
    @Mapping(source = "answer", target = "answer")
    @Mapping(source = "answeredTime", target = "answeredTime")
    @Mapping(target = "id", ignore = true)
    Answer convertUserAnswerDTOtoEntity(AnswerDTO answerDTO);

    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "questionId", target = "questionId")
    @Mapping(source = "answer", target = "answer")
    @Mapping(source = "answeredTime", target = "answeredTime")
    AnswerDTO convertUserAnswerEntitytoDTO(Answer answer);

    @Mapping(source = "gameId", target = "gameId")
    @Mapping(source = "invitingPlayerId", target = "invitingPlayerId")
    @Mapping(source = "invitingPlayerResult", target = "invitingPlayerResult")
    @Mapping(source = "invitedPlayerId", target = "invitedPlayerId")
    @Mapping(source = "invitedPlayerResult", target = "invitedPlayerResult")
    UserResultTupleDTO convertUserResultTupleEntitytoDTO(UserResultTuple userResultTuple);
}
