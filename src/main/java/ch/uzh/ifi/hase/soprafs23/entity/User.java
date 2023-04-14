package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Internal User Representation
 * This class composes the internal representation of the user and defines how
 * the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */

@Entity
@Table(name = "PLAYER")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)//, columnDefinition = "long default 0L")
    private Long points;
    @Column(nullable = false)
    private UserStatus status;
    @Column(nullable = false, unique = true)
    private String token;
    @Column(name="profile_picture", nullable = false)//, columnDefinition = "varchar(255) default 'John Snow'")
    private String profilePicture;
    @Column(name="background_music", nullable = false)//, columnDefinition = "boolean default true")
    private boolean backgroundMusic;

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

    public String getEmail() {
        return this.email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public Long getPoints() {
        return points;
    }
    public void setPoints(Long points) {
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

    public boolean getBackgroundMusic() {
        return backgroundMusic;
    }
    public void setBackgroundMusic(boolean backgroundMusic) {
        this.backgroundMusic = backgroundMusic;
    }
}
