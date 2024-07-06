package ru.yandex.practicum.filmorate.exception;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class ErrorResponse {
    final String error;
    final String description;
}
