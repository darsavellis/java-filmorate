package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review {
    @Positive
    long id;
    @NotBlank
    String content;
    @NotNull
    Boolean isPositive;
    @Positive
    long userId;
    @Positive
    long filmId;
    long useful = 0;
}
