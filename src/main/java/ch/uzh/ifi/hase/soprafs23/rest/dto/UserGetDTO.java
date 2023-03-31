package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;

import java.util.Date;


public class UserGetDTO {
    private Long id;
    private String username;
    private long points;
    private String profilePicture;
    private UserStatus status;
    private String token;

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public long getPoints() {
        return points;
    }
    public void setPoints(long points) {
        this.points = points;
    }

    public UserStatus getStatus() {
        return this.status;
    }
    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public String getToken() {
        return this.token;
    }
    public void setToken(String token) {
        this.token = token;
    }

    public String getProfilePicture() {
        return profilePicture;
    }
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}