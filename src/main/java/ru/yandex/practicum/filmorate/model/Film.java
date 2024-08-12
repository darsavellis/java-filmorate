package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.*;

@Data
@EqualsAndHashCode(of = "id")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
    long id;
    @NotBlank(message = "Название фильма не может быть пустой строкой/состоять из пробелов")
    String name;
    @Size(max = 200, message = "Количество символов в названии не должно превышать 200")
    String description;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate releaseDate;
    @Positive(message = "Длительность фильма должно быть положительным числом")
    long duration;
    MpaRating mpa = new MpaRating();
    Set<Genre> genres = new HashSet<>();
    Set<Long> likes = new HashSet<>();
    Set<Director> directors = new HashSet<>();
}
