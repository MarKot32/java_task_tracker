package pl.tasktracker.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Encja zadania.
 * - Tagi przechowywane w HashSet – unikalność, O(1) wstawianie/sprawdzanie.
 * - equals() i hashCode() oparte na unikalnym ID.
 */
public class Task {

    private final String id;
    private String       title;
    private String       description;
    private Priority     priority;
    private Status       status;

    // HashSet zapewnia unikalność tagów w ramach zadania
    private final Set<String> tags = new HashSet<>();

    public Task(String id, String title, String description, Priority priority) {
        this.id          = id;
        this.title       = title;
        this.description = description;
        this.priority    = priority;
        this.status      = Status.TODO;
    }

    // --- Gettery / Settery ---

    public String   getId()          { return id; }
    public String   getTitle()       { return title; }
    public String   getDescription() { return description; }
    public Priority getPriority()    { return priority; }
    public Status   getStatus()      { return status; }

    public void setStatus(Status status)       { this.status = status; }
    public void setTitle(String title)         { this.title = title; }
    public void setDescription(String desc)    { this.description = desc; }
    public void setPriority(Priority priority) { this.priority = priority; }

    /** Zwraca niemodyfikowalny widok tagów. */
    public Set<String> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /** @return true jeśli tag był nowy (nie istniał wcześniej) */
    public boolean addTag(String tag) {
        return tags.add(tag.toLowerCase().trim());
    }

    // --- equals / hashCode ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task other)) return false;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Task{id='%s', tytuł='%s', priorytet=%s, status=%s, tagi=%s}",
                id, title, priority, status, tags);
    }
}
