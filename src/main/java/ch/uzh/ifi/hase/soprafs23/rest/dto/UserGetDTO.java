package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
public class UserGetDTO {
    private Long id;
    private String username;
    private Date creationDate;
    private UserStatus status;
    private Date birthdayDate;
    private String token;

}
