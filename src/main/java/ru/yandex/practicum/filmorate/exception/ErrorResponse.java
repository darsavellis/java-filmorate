package ru.yandex.practicum.filmorate.exception;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Data
public class ErrorResponse {
    String error;
    String description;
}
