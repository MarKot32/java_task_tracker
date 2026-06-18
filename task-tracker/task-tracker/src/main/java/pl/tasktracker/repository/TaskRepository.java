package pl.tasktracker.repository;

import pl.tasktracker.model.Status;
import pl.tasktracker.model.Task;
import pl.tasktracker.model.User;

import java.util.*;

/**
 * Warstwa dostępu do danych dla zadań.
 *
 * Zastosowane kolekcje:
 * - Map<String, Task>          (HashMap)   – O(1) lookup zadania po ID.
 * - Map<User, List<Task>>      (HashMap)   – przypisania użytkownik → lista zadań.
 * - Map<Status, List<Task>>    (EnumMap)   – grupowanie po statusie; EnumMap
 *                                            jest zoptymalizowany dla kluczy-enumów.
 * - Set<String>                (TreeSet)   – globalny zbiór tagów zawsze w porządku
 *                                            alfabetycznym (TreeSet = sorted).
 */
public class TaskRepository {

    // Klucz: ID zadania → O(1) wyszukiwanie
    private final Map<String, Task> tasksById = new HashMap<>();

    // Przypisania: użytkownik → lista jego zadań
    private final Map<User, List<Task>> tasksByUser = new HashMap<>();

    // Grupowanie po statusie
    private final Map<Status, List<Task>> tasksByStatus = new EnumMap<>(Status.class);

    // Globalny, zawsze posortowany alfabet. zbiór tagów
    private final Set<String> globalTags = new TreeSet<>();

    public TaskRepository() {
        // Inicjalizacja list dla każdego statusu
        for (Status s : Status.values()) {
            tasksByStatus.put(s, new ArrayList<>());
        }
    }

    // ------------------------------------------------------------------ ZAPIS

    /** @return true jeśli zadanie zostało dodane; false gdy ID już istnieje */
    public boolean save(Task task) {
        if (tasksById.containsKey(task.getId())) {
            return false;
        }
        tasksById.put(task.getId(), task);
        tasksByStatus.get(task.getStatus()).add(task);
        globalTags.addAll(task.getTags());
        return true;
    }

    // ------------------------------------------------------------------ WYSZUKIWANIE

    /** O(1) dzięki HashMap */
    public Optional<Task> findById(String id) {
        return Optional.ofNullable(tasksById.get(id));
    }

    public Collection<Task> findAll() {
        return Collections.unmodifiableCollection(tasksById.values());
    }

    // ------------------------------------------------------------------ STATUSY

    /**
     * Aktualizuje status zadania – przesuwa je między listami w tasksByStatus.
     */
    public void updateStatus(Task task, Status newStatus) {
        Status old = task.getStatus();
        if (old == newStatus) return;

        tasksByStatus.get(old).remove(task);
        task.setStatus(newStatus);
        tasksByStatus.get(newStatus).add(task);
    }

    /** Zwraca niemodyfikowalną mapę: status → lista zadań */
    public Map<Status, List<Task>> getTasksByStatus() {
        // Budujemy nową mapę z niemodyfikowalnymi listami
        Map<Status, List<Task>> result = new EnumMap<>(Status.class);
        tasksByStatus.forEach((s, list) ->
                result.put(s, Collections.unmodifiableList(new ArrayList<>(list))));
        return Collections.unmodifiableMap(result);
    }

    // ------------------------------------------------------------------ PRZYPISANIA

    public void assignToUser(Task task, User user) {
        tasksByUser.computeIfAbsent(user, u -> new ArrayList<>()).add(task);
    }

    /**
     * Zwraca zadania użytkownika posortowane malejąco po priorytecie (HIGH → LOW).
     * Użycie Stream API (Java 8+).
     */
    public List<Task> findByUser(User user) {
        List<Task> tasks = tasksByUser.getOrDefault(user, Collections.emptyList());
        return tasks.stream()
                .sorted(Comparator.comparing(Task::getPriority).reversed())
                .toList();   // Java 16+ unmodifiable list
    }

    // ------------------------------------------------------------------ TAGI

    /** Dodaje tag do zadania i odświeża globalny TreeSet */
    public boolean addTagToTask(Task task, String tag) {
        boolean added = task.addTag(tag);
        if (added) globalTags.add(tag.toLowerCase().trim());
        return added;
    }

    /** Zwraca posortowany alfabetycznie zbiór wszystkich tagów w systemie. */
    public Set<String> getGlobalTags() {
        return Collections.unmodifiableSet(globalTags);
    }

    // ------------------------------------------------------------------ WALIDACJA

    public boolean existsById(String id) {
        return tasksById.containsKey(id);
    }
}
