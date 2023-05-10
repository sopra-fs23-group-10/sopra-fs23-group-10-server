package ch.uzh.ifi.hase.soprafs23.rest.dto;

public class UserPutDTO {

    private String username;
    private String profilePicture;

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePicture() {
        return profilePicture;
    }
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

}
