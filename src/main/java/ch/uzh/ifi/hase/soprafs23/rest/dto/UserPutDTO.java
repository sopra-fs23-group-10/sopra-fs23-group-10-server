package ch.uzh.ifi.hase.soprafs23.rest.dto;

import lombok.*;
import java.util.Date;

@Getter
@Setter
public class UserPutDTO {

    private String username;
    private Date birthdayDate;
}
