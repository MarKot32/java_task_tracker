package pl.tasktracker.service;

import pl.tasktracker.model.Priority;
import pl.tasktracker.model.Status;
import pl.tasktracker.model.Task;
import pl.tasktracker.model.User;
import pl.tasktracker.repository.HistoryRepository;
import pl.tasktracker.repository.TaskRepository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Warstwa serwisowa dla operacji na zadaniach.
 */
public class TaskService {

    private final TaskRepository    taskRepo;
    private final HistoryRepository history;

    public TaskService(TaskRepository taskRepo, HistoryRepository history) {
        this.taskRepo = taskRepo;
        this.history  = history;
    }

    // ------------------------------------------------------------------ CRUD

    /**
     * Tworzy i zapisuje nowe zadanie.
     * @throws TaskTrackerException gdy ID jest już zajęte lub dane niepoprawne
     */
    public Task addTask(String id, String title, String description, Priority priority) {
        if (id == null || id.isBlank())
            throw new TaskTrackerException("ID zadania nie może być puste.");
        if (title == null || title.isBlank())
            throw new TaskTrackerException("Tytuł zadania nie może być pusty.");
        if (taskRepo.existsById(id.trim()))
            throw new TaskTrackerException("Zadanie o ID '" + id + "' już istnieje.");

        Task task = new Task(id.trim(), title.trim(), description.trim(), priority);
        taskRepo.save(task);
        history.add("Dodano zadanie: " + task.getId() + " – \"" + task.getTitle() + "\"");
        return task;
    }

    /**
     * Zmienia status zadania.
     * @throws TaskTrackerException gdy zadanie nie istnieje
     */
    public void changeStatus(String taskId, Status newStatus) {
        Task task = findByIdOrThrow(taskId);
        Status old = task.getStatus();
        taskRepo.updateStatus(task, newStatus);
        history.add("Zmieniono status zadania '" + taskId + "': " + old + " → " + newStatus);
    }

    // ------------------------------------------------------------------ TAGI

    /**
     * Dodaje tag do zadania.
     * @return true jeśli tag był nowy; false jeśli już istniał
     */
    public boolean addTag(String taskId, String tag) {
        if (tag == null || tag.isBlank())
            throw new TaskTrackerException("Tag nie może być pusty.");
        Task task = findByIdOrThrow(taskId);
        boolean added = taskRepo.addTagToTask(task, tag);
        if (added) {
            history.add("Dodano tag '" + tag + "' do zadania '" + taskId + "'");
        }
        return added;
    }

    // ------------------------------------------------------------------ PRZYPISANIA

    /**
     * Przypisuje zadanie do użytkownika.
     */
    public void assignToUser(String taskId, User user) {
        Task task = findByIdOrThrow(taskId);
        taskRepo.assignToUser(task, user);
        history.add("Przypisano zadanie '" + taskId + "' do użytkownika " + user.getLogin());
    }

    // ------------------------------------------------------------------ ZAPYTANIA

    /** O(1) – HashMap pod spodem */
    public Task findByIdOrThrow(String taskId) {
        return taskRepo.findById(taskId)
                .orElseThrow(() -> new TaskTrackerException(
                        "Zadanie o ID '" + taskId + "' nie istnieje."));
    }

    /** Zadania użytkownika posortowane malejąco po priorytecie */
    public List<Task> getTasksForUser(User user) {
        return taskRepo.findByUser(user);
    }

    /** Mapa status → lista zadań */
    public Map<Status, List<Task>> getTasksGroupedByStatus() {
        return taskRepo.getTasksByStatus();
    }

    /** Globalny, posortowany alfabetycznie zbiór tagów (TreeSet) */
    public Set<String> getAllTags() {
        return taskRepo.getGlobalTags();
    }

    public Collection<Task> getAllTasks() {
        return taskRepo.findAll();
    }
}
