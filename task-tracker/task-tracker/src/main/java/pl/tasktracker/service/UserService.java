package pl.tasktracker.service;

import pl.tasktracker.model.User;
import pl.tasktracker.repository.HistoryRepository;
import pl.tasktracker.repository.UserRepository;

import java.util.Collection;

/**
 * Warstwa serwisowa dla operacji na użytkownikach.
 * Odpowiada za logikę biznesową i rejestrację historii.
 */
public class UserService {

    private final UserRepository   userRepo;
    private final HistoryRepository history;

    public UserService(UserRepository userRepo, HistoryRepository history) {
        this.userRepo = userRepo;
        this.history  = history;
    }

    /**
     * Dodaje nowego użytkownika.
     * @throws TaskTrackerException gdy login jest już zajęty
     */
    public User addUser(String login, String firstName, String lastName) {
        if (login == null || login.isBlank())
            throw new TaskTrackerException("Login nie może być pusty.");
        if (firstName == null || firstName.isBlank())
            throw new TaskTrackerException("Imię nie może być puste.");
        if (lastName == null || lastName.isBlank())
            throw new TaskTrackerException("Nazwisko nie może być puste.");

        if (userRepo.existsByLogin(login)) {
            throw new TaskTrackerException("Login '" + login + "' jest już zajęty.");
        }

        User user = new User(login.trim(), firstName.trim(), lastName.trim());
        userRepo.save(user);
        history.add("Dodano użytkownika: " + user);
        return user;
    }

    /**
     * @throws TaskTrackerException gdy użytkownik nie istnieje
     */
    public User getByLogin(String login) {
        return userRepo.findByLogin(login)
                .orElseThrow(() -> new TaskTrackerException(
                        "Użytkownik o loginie '" + login + "' nie istnieje."));
    }

    public Collection<User> getAllUsers() {
        return userRepo.findAll();
    }
}
