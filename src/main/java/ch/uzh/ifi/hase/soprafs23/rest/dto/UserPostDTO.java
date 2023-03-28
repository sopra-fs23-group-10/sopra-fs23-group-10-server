package ch.uzh.ifi.hase.soprafs23.rest.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserPostDTO {

    private String username;
    private String password;
    private String email;
    private Long id;
}
