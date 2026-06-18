package pl.tasktracker.model;

public enum Priority {
    LOW, MEDIUM, HIGH;

    public static Priority fromString(String s) {
        return switch (s.trim().toUpperCase()) {
            case "LOW"    -> LOW;
            case "MEDIUM" -> MEDIUM;
            case "HIGH"   -> HIGH;
            default -> throw new IllegalArgumentException("Nieznany priorytet: " + s);
        };
    }
}
