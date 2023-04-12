package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.entity.UserAnswerTuple;
import ch.uzh.ifi.hase.soprafs23.entity.UserResultTuple;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
    void testUserAnswerTuple_fromEntityToDTO_success() {
        // create UserPutDTO
        UserAnswerTuple userAnswerTuple = new UserAnswerTuple(1L,"Is it ture?","True",10L);

        // MAP -> Create user
        UserAnswerDTO userAnswerTupleDTO = DTOMapper.INSTANCE.convertUserAnswerEntitytoDTO(userAnswerTuple);

        // check content
        assertEquals(userAnswerTuple.getUserId(), userAnswerTupleDTO.getUserId());
        assertEquals(userAnswerTuple.getQuestionId(), userAnswerTupleDTO.getQuestionId());
        assertEquals(userAnswerTuple.getAnswer(), userAnswerTupleDTO.getAnswer());
        assertEquals(userAnswerTuple.getAnsweredTime(), userAnswerTupleDTO.getAnsweredTime());
    }

    @Test
    void testUserAnswerTupleDTO_fromDTOToEntity_success() {
        // create UserPutDTO
        UserAnswerDTO userAnswerTupleDTO = new UserAnswerDTO();
        userAnswerTupleDTO.setUserId(1L);
        userAnswerTupleDTO.setQuestionId("1");
        userAnswerTupleDTO.setAnswer("True");
        userAnswerTupleDTO.setAnsweredTime(10L);

        // MAP -> Create user
        UserAnswerTuple userAnswerTuple = DTOMapper.INSTANCE.convertUserAnswerDTOtoEntity(userAnswerTupleDTO);

        // check content
        assertEquals(userAnswerTuple.getUserId(), userAnswerTupleDTO.getUserId());
        assertEquals(userAnswerTuple.getQuestionId(), userAnswerTupleDTO.getQuestionId());
        assertEquals(userAnswerTuple.getAnswer(), userAnswerTupleDTO.getAnswer());
        assertEquals(userAnswerTuple.getAnsweredTime(), userAnswerTupleDTO.getAnsweredTime());
    }
}
