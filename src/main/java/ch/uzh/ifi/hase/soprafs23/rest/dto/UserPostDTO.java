package ch.uzh.ifi.hase.soprafs23.rest.dto;

public class UserPostDTO {

    private String username;
    private String password;
    private Long id;
    public String getUsername() {
    return username;
  }
    public void setUsername(String username) {
    this.username = username;
  }
    public String getPassword() {
      return password;
  }
    public void setPassword(String password) {
      this.password = password;
  }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
}
