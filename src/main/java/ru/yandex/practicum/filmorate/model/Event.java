package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {
    long id;
    @Positive
    long userId;
    @NotNull
    EventType eventType;
    @Positive
    long entityId;
    @NotNull
    OperationType operationType;
    @NotNull
    Timestamp timestamp;
}
