package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.String.format;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private User user;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setId(1L);
        user.setUsername("testUsername");
        user.setPassword("testPassword");
        user.setPoints(2L);
        user.setEmail("email@email.com");
        user.setProfilePicture("testProfilePicture");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);
    }

// Tests of PostMappings
    @Test
    void createUser_nonexistentUser_thenUserCreated_201() throws Exception {
        // given
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("testUsername");
        userPostDTO.setPassword("testPassword");
        userPostDTO.setEmail("email@email.com");

        // set up userService to throw the exception
        given(userService.createUser(Mockito.any())).willReturn(user);


        // when/then -> build the post request
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest).andExpect(status().isCreated())
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.profilePicture", is(user.getProfilePicture())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())))
                .andExpect(jsonPath("$.points", is((int) user.getPoints())))
                .andExpect(jsonPath("$.token", is(user.getToken())))
                .andExpect(jsonPath("$.id", is((int) user.getId())))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.email").doesNotExist());
    }

    @Test
    void createUser_whenUserExists_thenThrowsConflict_409() throws Exception {
        // given
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("testUsername");
        userPostDTO.setPassword("testPassword");
        userPostDTO.setEmail("email@email.com");

        // set up userService to throw the exception
        when(userService.createUser(Mockito.any())).thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, format("The username provided is already taken. Therefore, the user could not be created!")));


        // when/then -> build the post request
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest).andExpect(status().isConflict());
    }

    @Test
    void createUser_whenEmptyUsername_thenThrowsForbidden_403() throws Exception {
        // given
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("");
        userPostDTO.setPassword("testPassword");
        userPostDTO.setEmail("email@email.com");

        // set up userService to throw the exception
        given(userService.createUser(Mockito.any())).willReturn(user);

        // when/then -> build the post request
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest).andExpect(status().isForbidden());
    }

    @Test
    void createUser_whenNullUsername_thenThrowsForbidden_403() throws Exception {
        // given
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername(null);
        userPostDTO.setPassword("testPassword");
        userPostDTO.setEmail("email@email.com");

        // set up userService to throw the exception
        given(userService.createUser(Mockito.any())).willReturn(user);

        // when/then -> build the post request
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest).andExpect(status().isForbidden());
    }

    @Test
    void createUser_whenEmptyPassword_thenThrowsForbidden_403() throws Exception {
        // given
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("testUsername");
        userPostDTO.setPassword("");
        userPostDTO.setEmail("email@email.com");

        // set up userService to throw the exception
        given(userService.createUser(Mockito.any())).willReturn(user);

        // when/then -> build the post request
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest).andExpect(status().isForbidden());
    }

    @Test
    void createUser_whenNullPassword_thenThrowsForbidden_403() throws Exception {
        // given
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("testUsername");
        userPostDTO.setPassword(null);
        userPostDTO.setEmail("email@email.com");

        // set up userService to throw the exception
        given(userService.createUser(Mockito.any())).willReturn(user);

        // when/then -> build the post request
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest).andExpect(status().isForbidden());
    }

    @Test
    void createUser_whenEmptyEmail_thenThrowsForbidden_403() throws Exception {
        // given
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("testUsername");
        userPostDTO.setPassword("testPassword");
        userPostDTO.setEmail("");

        // set up userService to throw the exception
        given(userService.createUser(Mockito.any())).willReturn(user);

        // when/then -> build the post request
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest).andExpect(status().isForbidden());
    }

    @Test
    void createUser_whenNullEmail_thenThrowsForbidden_403() throws Exception {
        // given
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("testUsername");
        userPostDTO.setPassword("testPassword");
        userPostDTO.setEmail(null);

        // set up userService to throw the exception
        given(userService.createUser(Mockito.any())).willReturn(user);

        // when/then -> build the post request
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest).andExpect(status().isForbidden());
    }


// Tests of GetMappings
    @Test
    void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
        // given
        List<User> allUsers = Collections.singletonList(user);

        // this mocks the UserService -> we define above what the userService should
        // return when getUsers() is called
        given(userService.verifyToken(user.getToken())).willReturn(user);
        given(userService.getUsers()).willReturn(allUsers);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users")
                .header("token", user.getToken());

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is(user.getUsername())))
                .andExpect(jsonPath("$[0].profilePicture", is(user.getProfilePicture())))
                .andExpect(jsonPath("$[0].status", is(user.getStatus().toString())))
                .andExpect(jsonPath("$[0].points", is((int) user.getPoints())))
                .andExpect(jsonPath("$[0].token").isEmpty())
                .andExpect(jsonPath("$[0].id", is((int) user.getId())))
                .andExpect(jsonPath("$[0].password").doesNotExist())
                .andExpect(jsonPath("$[0].email").doesNotExist());
    }

    @Test
    void givenUser_whenGetUser_thenReturnUser_200() throws Exception {
        // this mocks the UserService -> we define above what the userService should
        // return when getUsers() is called
        given(userService.verifyToken(user.getToken())).willReturn(user);
        given(userService.searchUserById(user.getId())).willReturn(user);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users/" + user.getId())
                .header("token", user.getToken());

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.profilePicture", is(user.getProfilePicture())))
                .andExpect(jsonPath("$.id", is((int) user.getId())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())))
                .andExpect(jsonPath("$.points", is((int) user.getPoints())))
                .andExpect(jsonPath("$.token").isEmpty())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.email").doesNotExist());
    }

    @Test
    void givenNonexistentUser_whenGetUser_thenThrowsNotFound_404() throws Exception {
    // given
        given(userService.verifyToken(user.getToken())).willReturn(user);
        when(userService.searchUserById(user.getId())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User with specified ID does not exist."));

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/users/" + user.getId())
                .header("token", user.getToken());

        // then
        mockMvc.perform(getRequest).andExpect(status().isNotFound());
    }

    @Test
    void givenUser_whenInvalidToken_thenThrowUnauthorized_401() throws Exception {
        // given
        String invalidToken = "someInvalidToken";

        // this mocks the UserService -> we define above what the userService should
        // return when getUsers() is called
        when(userService.verifyToken(invalidToken)).thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Provided token is invalid."));
        given(userService.searchUserById(user.getId())).willReturn(user);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users/" + user.getId())
                .header("token", invalidToken);

        // then
        mockMvc.perform(getRequest).andExpect(status().isUnauthorized());
    }

    @Test
    void givenUser_whenMissingToken_thenThrowBadRequest_400() throws Exception {
        // given
        // this mocks the UserService -> we define above what the userService should
        // return when getUsers() is called
        given(userService.verifyToken(Mockito.any())).willReturn(user);
        given(userService.searchUserById(user.getId())).willReturn(user);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users/" + user.getId());

        // then
        mockMvc.perform(getRequest).andExpect(status().isBadRequest());
    }

    @Test
    void getOnlineUsers_allValid_thenReturnsOnlineUsers_200() throws Exception {
        user.setStatus(UserStatus.ONLINE);
        List<User> onlineList = new ArrayList<>();
        onlineList.add(user);

        given(userService.verifyToken(user.getToken())).willReturn(user);
        given(userService.getOnlineUsers()).willReturn(onlineList);

        MockHttpServletRequestBuilder getRequest = get("/users/online")
                .header("token", user.getToken());

        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is(user.getUsername())))
                .andExpect(jsonPath("$[0].profilePicture", is(user.getProfilePicture())))
                .andExpect(jsonPath("$[0].status", is(user.getStatus().toString())))
                .andExpect(jsonPath("$[0].points", is((int) user.getPoints())))
                .andExpect(jsonPath("$[0].token").isEmpty())
                .andExpect(jsonPath("$[0].id", is((int) user.getId())))
                .andExpect(jsonPath("$[0].password").doesNotExist())
                .andExpect(jsonPath("$[0].email").doesNotExist());
    }

    @Test
    void getOnlineUsers_invalidToken_thenThrowsUnauthorized_401() throws Exception {
        user.setStatus(UserStatus.ONLINE);
        List<User> onlineList = new ArrayList<>();
        onlineList.add(user);

        given(userService.verifyToken(user.getToken())).willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Provided token is invalid."));
        given(userService.getOnlineUsers()).willReturn(onlineList);

        MockHttpServletRequestBuilder getRequest = get("/users/online")
                .header("token", user.getToken());

        mockMvc.perform(getRequest).andExpect(status().isUnauthorized());
    }

// Tests of PutMappings
    @Test
    void givenPutUser_whenUserExists_thenThrowsNoContent_204() throws Exception {
        // given
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("changedUsername");
        userPutDTO.setProfilePicture("changedProfilePicture");

        // this mocks the UserService -> we define above what the userService should
        // return when getUsers() is called
        given(userService.verifyTokenWithId(user.getToken(), user.getId())).willReturn(user);
        given(userService.changeUsernameAndProfilePic(Mockito.anyLong(), Mockito.any())).willReturn(user);
        // when
        MockHttpServletRequestBuilder putRequest = put("/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO))
                .header("token", user.getToken());

        // then
        mockMvc.perform(putRequest).andExpect(status().isNoContent())
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.profilePicture", is(user.getProfilePicture())))
                .andExpect(jsonPath("$.id", is((int) user.getId())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())))
                .andExpect(jsonPath("$.points", is((int) user.getPoints())))
                .andExpect(jsonPath("$.token").isEmpty())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.email").doesNotExist());
    }

    @Test
    void givenPutUser_whenWrongUserId_thenThrowsNotFound_404() throws Exception {
        // given
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("changedUsername");

        long invalidID = 56;

        // this mocks the UserService -> we define above what the userService should
        // return when getUsers() is called
        given(userService.changeUsernameAndProfilePic(Mockito.anyLong(), Mockito.any())).willReturn(user);
        when(userService.verifyTokenWithId(user.getToken(), invalidID)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User with specified ID does not exist."));

        // when
        MockHttpServletRequestBuilder putRequest = put("/users/" + invalidID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO))
                .header("token", user.getToken());

        // then
        mockMvc.perform(putRequest).andExpect(status().isNotFound());
    }

    @Test
    void givenPutUser_whenNoToken_thenThrowsBadRequest_400() throws Exception {
        // given
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("changedUsername");

        // this mocks the UserService -> we define above what the userService should
        // return when getUsers() is called
        given(userService.changeUsernameAndProfilePic(Mockito.anyLong(), Mockito.any())).willReturn(user);
        when(userService.verifyTokenWithId(user.getToken(), user.getId())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User with specified ID does not exist."));

        // when
        MockHttpServletRequestBuilder putRequest = put("/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        // then
        mockMvc.perform(putRequest).andExpect(status().isBadRequest());
    }

    @Test
    void givenPutUser_whenInvalidToken_thenThrowsUnauthorized_401() throws Exception {
        // given
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("changedUsername");

        String invalidToken = "SomeRandomTokenThatDoesNotExist";

        // this mocks the UserService -> we define above what the userService should
        // return when getUsers() is called
        given(userService.changeUsernameAndProfilePic(Mockito.anyLong(), Mockito.any())).willReturn(user);
        when(userService.verifyTokenWithId(invalidToken, user.getId())).thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Provided token is invalid."));

        // when
        MockHttpServletRequestBuilder putRequest = put("/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO))
                .header("token", invalidToken);

        // then
        mockMvc.perform(putRequest).andExpect(status().isUnauthorized());
    }

    @Test
    void givenPutUser_whenNotMatchingID_thenThrowsUnauthorized_401() throws Exception {
        // given
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("changedUsername");

        long offsetId = user.getId() + 1;

        // this mocks the UserService -> we define above what the userService should
        // return when getUsers() is called
        given(userService.changeUsernameAndProfilePic(Mockito.anyLong(), Mockito.any())).willReturn(user);
        when(userService.verifyTokenWithId(user.getToken(), offsetId)).thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized."));

        // when
        MockHttpServletRequestBuilder putRequest = put("/users/" + offsetId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO))
                .header("token", user.getToken());

        // then
        mockMvc.perform(putRequest).andExpect(status().isUnauthorized());
    }

    @Test
    void givenPutUser_whenNullUsername_thenThrowsBadRequest_400() throws Exception {
        // given
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername(null);

        // this mocks the UserService -> we define above what the userService should
        // return when getUsers() is called
        given(userService.verifyTokenWithId(user.getToken(), user.getId())).willReturn(user);
        given(userService.changeUsernameAndProfilePic(Mockito.anyLong(), Mockito.any())).willReturn(user);

        // when
        MockHttpServletRequestBuilder putRequest = put("/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO))
                .header("token", user.getToken());

        // then
        mockMvc.perform(putRequest).andExpect(status().isBadRequest());
    }

    @Test
    void givenPutUser_whenEmptyUsername_thenThrowsBadRequest_400() throws Exception {
        // given
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("");

        // this mocks the UserService -> we define above what the userService should
        // return when getUsers() is called
        given(userService.verifyTokenWithId(user.getToken(), user.getId())).willReturn(user);
        given(userService.changeUsernameAndProfilePic(Mockito.anyLong(), Mockito.any())).willReturn(user);

        // when
        MockHttpServletRequestBuilder putRequest = put("/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO))
                .header("token", user.getToken());

        // then
        mockMvc.perform(putRequest).andExpect(status().isBadRequest());
    }


    @Test
    void login_whenValid_thenReturnUserWithToken_200() throws Exception {
        user.setStatus(UserStatus.OFFLINE);

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername(user.getUsername());
        userPostDTO.setPassword(user.getPassword());

        given(userService.checkLoginCredentials(user.getUsername(), user.getPassword())).willReturn(user);

        MockHttpServletRequestBuilder postRequest = post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        mockMvc.perform(postRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.profilePicture", is(user.getProfilePicture())))
                .andExpect(jsonPath("$.id", is((int) user.getId())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())))
                .andExpect(jsonPath("$.points", is((int) user.getPoints())))
                .andExpect(jsonPath("$.token", is(user.getToken())))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.email").doesNotExist());
    }


    @Test
    void login_whenEmptyUsername_thenThrowForbidden_403() throws Exception {
        user.setStatus(UserStatus.OFFLINE);

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("");
        userPostDTO.setPassword(user.getPassword());

        given(userService.checkLoginCredentials(userPostDTO.getUsername(), userPostDTO.getPassword())).willReturn(user);
        doCallRealMethod().when(userService).setOnline(user.getId());


        MockHttpServletRequestBuilder postRequest = post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        mockMvc.perform(postRequest).andExpect(status().isForbidden());
    }

    @Test
    void login_whenNullUsername_thenThrowForbidden_403() throws Exception {
        user.setStatus(UserStatus.OFFLINE);

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername(null);
        userPostDTO.setPassword(user.getPassword());

        given(userService.checkLoginCredentials(userPostDTO.getUsername(), userPostDTO.getPassword())).willReturn(user);
        doCallRealMethod().when(userService).setOnline(user.getId());


        MockHttpServletRequestBuilder postRequest = post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        mockMvc.perform(postRequest).andExpect(status().isForbidden());
    }

    @Test
    void login_whenEmptyPassword_thenThrowForbidden_403() throws Exception {
        user.setStatus(UserStatus.OFFLINE);

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername(user.getUsername());
        userPostDTO.setPassword("");

        given(userService.checkLoginCredentials(userPostDTO.getUsername(), userPostDTO.getPassword())).willReturn(user);
        doCallRealMethod().when(userService).setOnline(user.getId());


        MockHttpServletRequestBuilder postRequest = post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        mockMvc.perform(postRequest).andExpect(status().isForbidden());
    }

    @Test
    void login_whenNullPassword_thenThrowForbidden_403() throws Exception {
        user.setStatus(UserStatus.OFFLINE);

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername(user.getUsername());
        userPostDTO.setPassword(null);

        given(userService.checkLoginCredentials(userPostDTO.getUsername(), userPostDTO.getPassword())).willReturn(user);
        doCallRealMethod().when(userService).setOnline(user.getId());


        MockHttpServletRequestBuilder postRequest = post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        mockMvc.perform(postRequest).andExpect(status().isForbidden());
    }

    @Test
    void givenUser_whenLogout_thenReturnUser_200() throws Exception {
        // this mocks the UserService -> we define above what the userService should
        // return when getUsers() is called
        given(userService.verifyTokenWithId(user.getToken(), user.getId())).willReturn(user);
        given(userService.searchUserById(user.getId())).willReturn(user);

        // when
        MockHttpServletRequestBuilder postRequest = post("/logout/" + user.getId())
                .header("token", user.getToken());

        // then
        mockMvc.perform(postRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
    }

    @Test
    void logout_whenNotLoggedIn_thenThrowUnauthorized_401() throws Exception {
        // this mocks the UserService -> we define above what the userService should
        // return when getUsers() is called
        given(userService.verifyTokenWithId(user.getToken(), user.getId())).willReturn(user);
        Mockito.doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized."))
                .when(userService).setOffline(user.getId());
        // when
        MockHttpServletRequestBuilder postRequest = post("/logout/" + user.getId())
                .header("token", user.getToken());

        // then
        mockMvc.perform(postRequest).andExpect(status().isUnauthorized());
    }

    @Test
    void logout_whenNoValidToken_thenThrowUnauthorized_401() throws Exception {
        // this mocks the UserService -> we define above what the userService should
        // return when getUsers() is called
        given(userService.verifyTokenWithId(user.getToken(), user.getId())).willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized."));
        // when
        MockHttpServletRequestBuilder postRequest = post("/logout/" + user.getId())
                .header("token", user.getToken());
        // then
        mockMvc.perform(postRequest).andExpect(status().isUnauthorized());
    }

  private String asJsonString(final Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          format("The request body could not be created.%s", e.toString()));
    }
  }
}