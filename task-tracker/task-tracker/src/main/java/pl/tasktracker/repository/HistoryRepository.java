package pl.tasktracker.repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Przechowuje historię operacji w kolejności chronologicznej.
 *
 * Zastosowana kolekcja:
 * - List<String> (ArrayList) – zachowanie kolejności wstawiania, O(1) append.
 */
public class HistoryRepository {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ArrayList – chronologiczna historia, dodajemy tylko na koniec → O(1)
    private final List<String> entries = new ArrayList<>();

    public void add(String message) {
        String timestamp = LocalDateTime.now().format(FMT);
        entries.add("[" + timestamp + "] " + message);
    }

    /** Zwraca niemodyfikowalną listę wpisów w kolejności chronologicznej. */
    public List<String> getAll() {
        return Collections.unmodifiableList(entries);
    }
}
