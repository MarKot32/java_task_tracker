package pl.tasktracker.model;

import java.util.Objects;

/**
 * Encja użytkownika systemu.
 * equals() i hashCode() oparte na unikalnym loginie –
 * dzięki temu User może być kluczem w HashMap i elementem HashSet.
 */
public class User {

    private final String login;
    private String firstName;
    private String lastName;

    public User(String login, String firstName, String lastName) {
        this.login     = login;
        this.firstName = firstName;
        this.lastName  = lastName;
    }

    // --- Gettery ---

    public String getLogin()     { return login; }
    public String getFirstName() { return firstName; }
    public String getLastName()  { return lastName; }

    // --- equals / hashCode ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User other)) return false;
        return Objects.equals(login, other.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s %s", login, firstName, lastName);
    }
}
