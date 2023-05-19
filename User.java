import java.io.Serializable;

/**
 * User class
 *
 * This class stores the basic information that all users share. Seller and Customer extend this class.
 *
 * @author Charlie Schmidt, lab sec 002
 * @version 11/29/2022
 */
public class User implements Serializable {
    private String username;
    private String password;

    // constructor to create new User instance (no longer handles file i/o)
    public User(String username, String password) {
        this.password = password;
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return String.format("%s;%s", username, password);
    }

}
