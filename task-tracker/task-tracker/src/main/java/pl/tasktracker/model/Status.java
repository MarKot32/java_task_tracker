package pl.tasktracker.model;

public enum Status {
    TODO, IN_PROGRESS, DONE;

    public static Status fromString(String s) {
        return switch (s.trim().toUpperCase()) {
            case "TODO"        -> TODO;
            case "IN_PROGRESS" -> IN_PROGRESS;
            case "DONE"        -> DONE;
            default -> throw new IllegalArgumentException("Nieznany status: " + s);
        };
    }
}
