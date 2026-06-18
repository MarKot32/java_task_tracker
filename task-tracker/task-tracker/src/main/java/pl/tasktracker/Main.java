package pl.tasktracker;

import pl.tasktracker.repository.HistoryRepository;
import pl.tasktracker.repository.TaskRepository;
import pl.tasktracker.repository.UserRepository;
import pl.tasktracker.service.TaskService;
import pl.tasktracker.service.UserService;
import pl.tasktracker.ui.ConsoleUI;

/**
 * Punkt wejścia aplikacji.
 * Ręczna kompozycja zależności (DI bez frameworka).
 */
public class Main {

    public static void main(String[] args) {

        // --- Repozytoria ---
        HistoryRepository history  = new HistoryRepository();
        UserRepository    userRepo = new UserRepository();
        TaskRepository    taskRepo = new TaskRepository();

        // --- Serwisy ---
        UserService userService = new UserService(userRepo, history);
        TaskService taskService = new TaskService(taskRepo, history);

        // --- UI ---
        ConsoleUI ui = new ConsoleUI(userService, taskService, history);
        ui.start();
    }
}
