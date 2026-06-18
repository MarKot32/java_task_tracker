package pl.tasktracker.repository;

import pl.tasktracker.model.User;

import java.util.*;

/**
 * Warstwa dostępu do danych dla użytkowników.
 *
 * Zastosowane kolekcje:
 * - Map<String, User> (HashMap)  – O(1) lookup po loginie; login jako klucz.
 * - Set<String>       (HashSet)  – szybka weryfikacja unikalności loginu O(1).
 */
public class UserRepository {

    // Klucz: login (String), Wartość: obiekt User
    private final Map<String, User> usersByLogin = new HashMap<>();

    // Oddzielny zbiór loginów – szybka weryfikacja bez tworzenia User
    private final Set<String> loginSet = new HashSet<>();

    /** @return true jeśli użytkownik został dodany; false gdy login zajęty */
    public boolean save(User user) {
        if (loginSet.contains(user.getLogin())) {
            return false;
        }
        usersByLogin.put(user.getLogin(), user);
        loginSet.add(user.getLogin());
        return true;
    }

    public Optional<User> findByLogin(String login) {
        return Optional.ofNullable(usersByLogin.get(login));
    }

    public boolean existsByLogin(String login) {
        return loginSet.contains(login);
    }

    /** Zwraca niemodyfikowalną kolekcję wszystkich użytkowników. */
    public Collection<User> findAll() {
        return Collections.unmodifiableCollection(usersByLogin.values());
    }
}
