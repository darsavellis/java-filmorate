package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
import java.time.Instant;

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
    OperationType operation;
    // Это ужасный костыль, но тесты postman ждут число, а не дату
    @Min(1670590017281L)
    long timestamp;
}
