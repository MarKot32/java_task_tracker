package pl.tasktracker.service;

/** Bazowy wyjątek aplikacyjny – obejmuje wszelkie błędy logiki biznesowej. */
public class TaskTrackerException extends RuntimeException {
    public TaskTrackerException(String message) {
        super(message);
    }
}
