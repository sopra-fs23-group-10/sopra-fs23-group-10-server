package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserGetDTO {
    private long id;
    private String username;
    private long points;
    private String profilePicture;
    private UserStatus status;
    private String token;
    private long rank;
}
