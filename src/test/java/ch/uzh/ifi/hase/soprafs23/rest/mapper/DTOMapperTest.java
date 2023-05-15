package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.constant.Category;
import ch.uzh.ifi.hase.soprafs23.constant.ModeType;
import ch.uzh.ifi.hase.soprafs23.constant.QuizType;
import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DTOMapperTest
 * Tests if the mapping between the internal and the external/API representation
 * works.
 */
class DTOMapperTest {
  @Test
  void testCreateUser_fromUserPostDTO_toUser_success() {
    // create UserPostDTO
    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setUsername("username");
    userPostDTO.setPassword("password");
    userPostDTO.setEmail("email@email.com");

    // MAP -> Create user
    User user = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // check content
    assertEquals(userPostDTO.getUsername(), user.getUsername());
    assertEquals(userPostDTO.getPassword(), user.getPassword());
    assertEquals(userPostDTO.getEmail(), user.getEmail());
  }

  @Test
  void testGetUser_fromUser_toUserGetDTO_success() {
    // create User
    User user = new User();
    user.setUsername("firstname@lastname");
    user.setPassword("testPassword");
    user.setEmail("email@email.com");
    user.setProfilePicture("firstname@lastname");
    user.setStatus(UserStatus.OFFLINE);
    user.setToken("1");

    // MAP -> Create UserGetDTO
    UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

    // check content
    assertEquals(user.getId(), userGetDTO.getId());
    assertEquals(user.getUsername(), userGetDTO.getUsername());
    assertEquals(user.getProfilePicture(), userGetDTO.getProfilePicture());
    assertEquals(user.getStatus(), userGetDTO.getStatus());
    assertEquals(user.getToken(), userGetDTO.getToken());
  }

    @Test
    void testGetUser_fromUser_toUserGetDTONoToken_success() {
        // create User
        User user = new User();
        user.setUsername("firstname@lastname");
        user.setPassword("testPassword");
        user.setEmail("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);
        user.setId(1L);
        user.setToken("1");
        user.setPassword("testPassword");

        // MAP -> Create UserGetDTO
        UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTONoToken(user);

        // check content
        assertEquals(user.getId(), userGetDTO.getId());
        assertEquals(user.getUsername(), userGetDTO.getUsername());
        assertEquals(user.getStatus(), userGetDTO.getStatus());
        assertEquals(user.getProfilePicture(), userGetDTO.getProfilePicture());
        assertNull(userGetDTO.getToken());
    }

    @Test
    void testCreateUser_fromUserPutDTO_toUser_success() {
        // create UserPutDTO
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("username");

        // MAP -> Create user
        User user = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);

        // check content
        assertEquals(userPutDTO.getUsername(), user.getUsername());
    }
    @Test
    void testGame_fromEntity_toDTO_success() {
        Game game = new Game();
        game.setInvitingUserId(1L);
        game.setInvitedUserId(2L);
        game.setQuizType(QuizType.IMAGE);
        game.setModeType(ModeType.SINGLE);
        game.setGameId(1L);

        GameDTO gameDTO = DTOMapper.INSTANCE.convertGameEntityToPostDTO(game);

        assertEquals(game.getInvitingUserId(), gameDTO.getInvitingUserId());
        assertEquals(game.getInvitedUserId(), gameDTO.getInvitedUserId());
        assertEquals(game.getQuizType(), gameDTO.getQuizType());
        assertEquals(game.getModeType(), gameDTO.getModeType());
        assertEquals(game.getGameId(), gameDTO.getGameId());
    }

    @Test
    void testGame_fromDTO_toEntity_success() {
        GameDTO gameDTO = new GameDTO();
        gameDTO.setGameId(1L);
        gameDTO.setInvitingUserId(1L);
        gameDTO.setInvitedUserId(2L);
        gameDTO.setQuizType(QuizType.IMAGE);
        gameDTO.setModeType(ModeType.SINGLE);

        Game game = DTOMapper.INSTANCE.convertGamePostDTOtoEntity(gameDTO);

        assertEquals(gameDTO.getGameId(), game.getGameId());
        assertEquals(gameDTO.getInvitingUserId(), game.getInvitingUserId());
        assertEquals(gameDTO.getInvitedUserId(), game.getInvitedUserId());
        assertEquals(gameDTO.getQuizType(), game.getQuizType());
        assertEquals(gameDTO.getModeType(), game.getModeType());
    }

    @Test
    void testQuestion_fromEntity_toDTO_success() {
        Question question = new Question();
        question.setQuestionId(1L);
        question.setCategory(Category.MUSIC);
        question.setApiId("questionId");
        question.setQuestionString("Who do music?");
        question.setAllAnswers(java.util.Arrays.asList("Wlarbenborb", "Herbert Cheese", "Frank Sinotra", "John Lennon"));
        question.setCorrectAnswer("John Lennon");
        question.setIncorrectAnswers(java.util.Arrays.asList("Wlarbenborb", "Herbert Cheese", "Frank Sinotra"));

        QuestionDTO questionDTO = DTOMapper.INSTANCE.convertQuestionEntityToDTO(question);

        assertEquals(question.getApiId(), questionDTO.getApiId());
        assertEquals(question.getCategory(), questionDTO.getCategory());
        assertEquals(question.getQuestionString(), questionDTO.getQuestion());
        assertEquals(question.getAllAnswers(), questionDTO.getAllAnswers());
        assertNull(questionDTO.getCorrectAnswer());
        assertNull(questionDTO.getIncorrectAnswers());
    }

    @Test
    void testUserResultTuple_fromEntityToDTO_success() {
        // create UserPutDTO
        UserResultTuple userResultTuple = new UserResultTuple(1L,1L,2L);

        // MAP -> Create user
        UserResultTupleDTO userResultTupleDTO = DTOMapper.INSTANCE.convertUserResultTupleEntitytoDTO(userResultTuple);

        // check content
        assertEquals(userResultTuple.getGameId(), userResultTupleDTO.getGameId());
        assertEquals(userResultTuple.getInvitingPlayerId(), userResultTupleDTO.getInvitingPlayerId());
        assertEquals(userResultTuple.getInvitingPlayerResult(), userResultTupleDTO.getInvitingPlayerResult());
        assertEquals(userResultTuple.getInvitedPlayerId(), userResultTupleDTO.getInvitedPlayerId());
        assertEquals(userResultTuple.getInvitedPlayerResult(), userResultTupleDTO.getInvitedPlayerResult());
    }

    @Test
    void testUserResultTuple_fromEntity_toDTO_success() {
        UserResultTuple userResultTuple = new UserResultTuple();
        userResultTuple.setGameId(1L);
        userResultTuple.setInvitingPlayerId(1L);
        userResultTuple.setInvitingPlayerResult(200L);
        userResultTuple.setInvitedPlayerId(2L);
        userResultTuple.setInvitedPlayerResult(400L);

        UserResultTupleDTO userResultTupleDTO = DTOMapper.INSTANCE.convertUserResultTupleEntitytoDTO(userResultTuple);

        assertEquals(userResultTuple.getGameId(), userResultTupleDTO.getGameId());
        assertEquals(userResultTuple.getInvitingPlayerId(), userResultTupleDTO.getInvitingPlayerId());
        assertEquals(userResultTuple.getInvitingPlayerResult(), userResultTupleDTO.getInvitingPlayerResult());
        assertEquals(userResultTuple.getInvitedPlayerId(), userResultTupleDTO.getInvitedPlayerId());
        assertEquals(userResultTuple.getInvitedPlayerResult(), userResultTupleDTO.getInvitedPlayerResult());
    }
}
