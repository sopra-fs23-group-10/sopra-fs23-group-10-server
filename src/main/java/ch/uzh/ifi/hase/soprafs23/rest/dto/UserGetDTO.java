package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;

import java.time.LocalDateTime;
import java.util.Date;

public class UserGetDTO {

    private Long id;
    private String username;
    private Date creationDate;
    private UserStatus status;
    private Date birthdayDate;
    private String token;

    public Long getId() {
    return id;
    }
    public void setId(Long id) {
    this.id = id;
    }
    public String getUsername() {
    return username;
    }
    public void setUsername(String username) {
    this.username = username;
    }
    public Date getCreationDate() {
      return this.creationDate;
    }
    public void setCreationDate(Date creationDate) {
      this.creationDate = creationDate;
    }
    public UserStatus getStatus() {
    return status;
    }
    public void setStatus(UserStatus status) {
    this.status = status;
    }
    public Date getBirthdayDate() {
    return this.birthdayDate;
    }
    public void setBirthdayDate(Date birthdayDate) {
    this.birthdayDate = birthdayDate;
    }
    public String getToken() {
        return this.token;
    }
    public void setToken(String token) {
        this.token = token;
    }
}
