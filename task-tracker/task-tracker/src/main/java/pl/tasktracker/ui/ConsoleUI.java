package pl.tasktracker.ui;

import pl.tasktracker.model.Priority;
import pl.tasktracker.model.Status;
import pl.tasktracker.model.Task;
import pl.tasktracker.model.User;
import pl.tasktracker.repository.HistoryRepository;
import pl.tasktracker.service.TaskService;
import pl.tasktracker.service.TaskTrackerException;
import pl.tasktracker.service.UserService;

import java.util.*;

/**
 * Warstwa UI – interaktywne menu konsolowe.
 * Odpowiada wyłącznie za I/O; całą logikę deleguje do serwisów.
 */
public class ConsoleUI {

    private static final String SEP = "─".repeat(55);

    private final Scanner        scanner;
    private final UserService    userService;
    private final TaskService    taskService;
    private final HistoryRepository history;

    public ConsoleUI(UserService userService,
                     TaskService taskService,
                     HistoryRepository history) {
        this.scanner     = new Scanner(System.in);
        this.userService = userService;
        this.taskService = taskService;
        this.history     = history;
    }

    // ================================================================== MAIN LOOP

    public void start() {
        printBanner();
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Wybór: ");
            System.out.println();
            running = handleMain(choice);
        }
        System.out.println("\nDo widzenia!");
        scanner.close();
    }

    private boolean handleMain(int choice) {
        return switch (choice) {
            case 1  -> { menuUsers();   yield true; }
            case 2  -> { menuTasks();   yield true; }
            case 3  -> { menuAssign();  yield true; }
            case 4  -> { menuReports(); yield true; }
            case 0  -> false;
            default -> { warn("Nieznana opcja."); yield true; }
        };
    }

    // ================================================================== MENU UŻYTKOWNICY

    private void menuUsers() {
        printHeader("ZARZĄDZANIE UŻYTKOWNIKAMI");
        System.out.println("  1. Dodaj użytkownika");
        System.out.println("  2. Wyświetl wszystkich użytkowników");
        System.out.println("  0. Powrót");
        int c = readInt("Wybór: ");
        switch (c) {
            case 1 -> addUser();
            case 2 -> listUsers();
            case 0 -> {}
            default -> warn("Nieznana opcja.");
        }
    }

    private void addUser() {
        System.out.println(SEP);
        String login     = readLine("Login:    ");
        String firstName = readLine("Imię:     ");
        String lastName  = readLine("Nazwisko: ");
        try {
            User u = userService.addUser(login, firstName, lastName);
            ok("Dodano użytkownika: " + u);
        } catch (TaskTrackerException e) {
            warn(e.getMessage());
        }
    }

    private void listUsers() {
        Collection<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("  (brak użytkowników)");
            return;
        }
        printHeader("LISTA UŻYTKOWNIKÓW");
        // Stream API – sortowanie po loginie
        users.stream()
                .sorted(Comparator.comparing(User::getLogin))
                .forEach(u -> System.out.println("  " + u));
    }

    // ================================================================== MENU ZADANIA

    private void menuTasks() {
        printHeader("ZARZĄDZANIE ZADANIAMI");
        System.out.println("  1. Dodaj zadanie");
        System.out.println("  2. Zmień status zadania");
        System.out.println("  3. Dodaj tag do zadania");
        System.out.println("  4. Wyświetl wszystkie zadania");
        System.out.println("  0. Powrót");
        int c = readInt("Wybór: ");
        switch (c) {
            case 1 -> addTask();
            case 2 -> changeStatus();
            case 3 -> addTag();
            case 4 -> listAllTasks();
            case 0 -> {}
            default -> warn("Nieznana opcja.");
        }
    }

    private void addTask() {
        System.out.println(SEP);
        String id   = readLine("ID zadania:  ");
        String title = readLine("Tytuł:       ");
        String desc  = readLine("Opis:        ");
        System.out.println("  Priorytety: LOW | MEDIUM | HIGH");
        Priority priority;
        try {
            priority = Priority.fromString(readLine("Priorytet:   "));
        } catch (IllegalArgumentException e) {
            warn(e.getMessage());
            return;
        }
        try {
            Task t = taskService.addTask(id, title, desc, priority);
            ok("Dodano zadanie: " + t);
        } catch (TaskTrackerException e) {
            warn(e.getMessage());
        }
    }

    private void changeStatus() {
        System.out.println(SEP);
        String taskId = readLine("ID zadania: ");
        System.out.println("  Statusy: TODO | IN_PROGRESS | DONE");
        Status status;
        try {
            status = Status.fromString(readLine("Nowy status: "));
        } catch (IllegalArgumentException e) {
            warn(e.getMessage());
            return;
        }
        try {
            taskService.changeStatus(taskId, status);
            ok("Status zmieniony.");
        } catch (TaskTrackerException e) {
            warn(e.getMessage());
        }
    }

    private void addTag() {
        System.out.println(SEP);
        String taskId = readLine("ID zadania: ");
        String tag    = readLine("Tag:        ");
        try {
            boolean added = taskService.addTag(taskId, tag);
            if (added) ok("Tag '" + tag + "' dodany.");
            else       warn("Tag '" + tag + "' już istnieje w tym zadaniu.");
        } catch (TaskTrackerException e) {
            warn(e.getMessage());
        }
    }

    private void listAllTasks() {
        Collection<Task> tasks = taskService.getAllTasks();
        if (tasks.isEmpty()) {
            System.out.println("  (brak zadań)");
            return;
        }
        printHeader("WSZYSTKIE ZADANIA");
        // Stream API – sortowanie po ID
        tasks.stream()
                .sorted(Comparator.comparing(Task::getId))
                .forEach(t -> printTask(t, "  "));
    }

    // ================================================================== MENU PRZYPISANIA

    private void menuAssign() {
        printHeader("PRZYPISANIA I RELACJE");
        System.out.println("  1. Przypisz zadanie do użytkownika");
        System.out.println("  2. Wyświetl zadania użytkownika");
        System.out.println("  0. Powrót");
        int c = readInt("Wybór: ");
        switch (c) {
            case 1 -> assignTask();
            case 2 -> listUserTasks();
            case 0 -> {}
            default -> warn("Nieznana opcja.");
        }
    }

    private void assignTask() {
        System.out.println(SEP);
        String taskId = readLine("ID zadania: ");
        String login  = readLine("Login użytkownika: ");
        try {
            User user = userService.getByLogin(login);
            taskService.assignToUser(taskId, user);
            ok("Przypisano zadanie '" + taskId + "' do użytkownika '" + login + "'.");
        } catch (TaskTrackerException e) {
            warn(e.getMessage());
        }
    }

    private void listUserTasks() {
        System.out.println(SEP);
        String login = readLine("Login użytkownika: ");
        try {
            User user = userService.getByLogin(login);
            List<Task> tasks = taskService.getTasksForUser(user);
            if (tasks.isEmpty()) {
                System.out.println("  Użytkownik nie ma przypisanych zadań.");
                return;
            }
            printHeader("ZADANIA UŻYTKOWNIKA: " + login + " (posortowane wg priorytetu)");
            tasks.forEach(t -> printTask(t, "  "));
        } catch (TaskTrackerException e) {
            warn(e.getMessage());
        }
    }

    // ================================================================== MENU RAPORTY

    private void menuReports() {
        printHeader("RAPORTOWANIE I FILTROWANIE");
        System.out.println("  1. Wyszukaj zadanie po ID");
        System.out.println("  2. Zadania pogrupowane po statusie");
        System.out.println("  3. Wszystkie unikalne tagi (alfabetycznie)");
        System.out.println("  4. Historia operacji");
        System.out.println("  0. Powrót");
        int c = readInt("Wybór: ");
        switch (c) {
            case 1 -> findById();
            case 2 -> tasksByStatus();
            case 3 -> listTags();
            case 4 -> listHistory();
            case 0 -> {}
            default -> warn("Nieznana opcja.");
        }
    }

    private void findById() {
        System.out.println(SEP);
        String id = readLine("ID zadania: ");
        try {
            Task t = taskService.findByIdOrThrow(id);
            printHeader("WYNIK WYSZUKIWANIA");
            printTask(t, "  ");
        } catch (TaskTrackerException e) {
            warn(e.getMessage());
        }
    }

    private void tasksByStatus() {
        printHeader("ZADANIA POGRUPOWANE PO STATUSIE");
        Map<Status, List<Task>> grouped = taskService.getTasksGroupedByStatus();
        for (Status s : Status.values()) {
            List<Task> list = grouped.get(s);
            System.out.printf("%n  ▶ %s (%d)%n", s, list.size());
            if (list.isEmpty()) {
                System.out.println("    (brak)");
            } else {
                list.forEach(t -> printTask(t, "    "));
            }
        }
    }

    private void listTags() {
        Set<String> tags = taskService.getAllTags();
        printHeader("UNIKALNE TAGI W SYSTEMIE");
        if (tags.isEmpty()) {
            System.out.println("  (brak tagów)");
            return;
        }
        // TreeSet – już posortowany alfabetycznie
        tags.forEach(tag -> System.out.println("  #" + tag));
    }

    private void listHistory() {
        printHeader("HISTORIA OPERACJI");
        List<String> entries = history.getAll();
        if (entries.isEmpty()) {
            System.out.println("  (brak wpisów)");
            return;
        }
        entries.forEach(e -> System.out.println("  " + e));
    }

    // ================================================================== HELPERS

    private void printBanner() {
        System.out.println();
        System.out.println("╔" + "═".repeat(53) + "╗");
        System.out.println("║       KONSOLOWY SYSTEM ZARZĄDZANIA ZADANIAMI      ║");
        System.out.println("║                   Task Tracker v1.0               ║");
        System.out.println("╚" + "═".repeat(53) + "╝");
    }

    private void printMainMenu() {
        System.out.println("\n" + SEP);
        System.out.println("  MENU GŁÓWNE");
        System.out.println(SEP);
        System.out.println("  1. Zarządzanie użytkownikami");
        System.out.println("  2. Zarządzanie zadaniami");
        System.out.println("  3. Przypisania i relacje");
        System.out.println("  4. Raportowanie i filtrowanie");
        System.out.println("  0. Wyjście");
        System.out.println(SEP);
    }

    private void printHeader(String title) {
        System.out.println("\n" + SEP);
        System.out.println("  " + title);
        System.out.println(SEP);
    }

    private void printTask(Task t, String indent) {
        System.out.printf("%sID: %-12s | %s | priorytet: %-6s | status: %s%n",
                indent, t.getId(), t.getTitle(), t.getPriority(), t.getStatus());
        if (!t.getTags().isEmpty()) {
            System.out.println(indent + "  Tagi: " + t.getTags());
        }
        if (t.getDescription() != null && !t.getDescription().isBlank()) {
            System.out.println(indent + "  Opis: " + t.getDescription());
        }
    }

    private void ok(String msg) {
        System.out.println("  ✔ " + msg);
    }

    private void warn(String msg) {
        System.out.println("  ✘ BŁĄD: " + msg);
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String line = scanner.nextLine().trim();
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                warn("Wpisz liczbę całkowitą.");
            }
        }
    }
}
