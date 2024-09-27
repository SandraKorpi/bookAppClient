package SandraKorpi;

public class LoginDto {
    private String userName; // Fältet ska vara userName
    private String password;

    // Konstruktor
    public LoginDto(String userName, String password) {
        this.userName = userName; // Korrekt referens till userName
        this.password = password;
    }

    // Getters och Setters
    public String getUserName() {
        return userName; // Korrekt referens till userName
    }

    public void setUserName(String userName) { // Ändrad till setUserName
        this.userName = userName; // Korrekt referens
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
