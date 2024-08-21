package ru.yandex.practicum.filmorate.model;

public enum OperationType {
    REMOVE("REMOVE"),
    ADD("ADD"),
    UPDATE("UPDATE");

    private final String title;

    OperationType(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }

    public static OperationType fromString(String value) {
        if (value.equals("REMOVE")) return REMOVE;
        if (value.equals("ADD")) return ADD;
        if (value.equals("UPDATE")) return UPDATE;
        return ADD;
    }
}
