package ru.yandex.practicum.filmorate.model;

public enum EventType {
    LIKE("LIKE"),
    REVIEW("REVIEW"),
    FRIEND("FRIEND");

    private final String title;

    EventType(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }

    public static EventType fromString(String value) {
        if (value.equals("LIKE")) return LIKE;
        if (value.equals("REVIEW")) return REVIEW;
        if (value.equals("FRIEND")) return FRIEND;
        return LIKE;
    }
}
