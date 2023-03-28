package ch.uzh.ifi.hase.soprafs23.rest.dto;

import java.util.Date;

public class UserPutDTO {

    private String username;
    private Date birthdayDate;

    public String getUsername() {
        return this.username;
    }

    public Date getBirthdayDate() {
        return this.birthdayDate;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setBirthdayDate(Date birthdayDate) {
        this.birthdayDate = birthdayDate;
    }
}
