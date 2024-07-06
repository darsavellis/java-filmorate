package ru.yandex.practicum.filmorate.validation;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Objects;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@UtilityClass
public class FilmValidator {
    public static void validate(Film film) {
        if (FilmValidator.isNameNotValid(film.getName())) {
            throw new ValidationException("Name should not be empty");
        }
        if (isDescriptionNotValid(film.getDescription())) {
            throw new ValidationException("Max description length - 200 symbols");
        }
        if (isReleaseDateNotValid(film.getReleaseDate())) {
            throw new ValidationException("Release date should be after 28 december 1895 year");
        }
        if (isDurationNotValid(film.getDuration())) {
            throw new ValidationException("Duration should be positive number");
        }
        log.info("Film validation passed");
    }

    static boolean isNameNotValid(String name) {
        return Objects.isNull(name) || name.isBlank();
    }

    static boolean isDescriptionNotValid(String description) {
        return Objects.isNull(description) || description.length() > 200;
    }

    static boolean isDurationNotValid(long duration) {
        return duration <= 0;
    }

    static boolean isReleaseDateNotValid(LocalDate releaseDate) {
        return Objects.isNull(releaseDate) || !releaseDate.isAfter(LocalDate.of(1895, 12, 28));
    }
}
