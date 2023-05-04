package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;


public class UserGetDTO {
    private long id;
    private String username;
    private long points;
    private String profilePicture;
    private UserStatus status;
    private String token;
    private long rank;

    public long getId() {
        return this.id;
    }
    public String getUsername() {
        return this.username;
    }
    public long getPoints() {
        return points;
    }
    public UserStatus getStatus() {
        return this.status;
    }
    public String getToken() {
        return this.token;
    }
    public String getProfilePicture() {
        return profilePicture;
    }
    public long getRank(){return this.rank;}

    public void setId(long id) {
        this.id = id;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPoints(long points) {this.points = points;}
    public void setStatus(UserStatus status) {
        this.status = status;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
    public void setRank(long rank){this.rank = rank;}
}
