package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilmService {
    final Map<Long, Film> films = new HashMap<>();

    public Collection<Film> getFilms() {
        return films.values();
    }

    public Film createFilm(Film film) {
        try {
            filmValidator(film);
            log.info("Validation passed");
            film.setId(getNextID());
            log.debug("ID {} assigned to new film", film.getId());
            films.put(film.getId(), film);
            log.info("Film with ID {} is added to storage", film.getId());
            return film;
        } catch (Exception exception) {
            log.warn(exception.getMessage(), exception);
            throw exception;
        }
    }

    public Film updateFilm(Film newFilm) {
        try {
            if (films.containsKey(newFilm.getId())) {
                filmValidator(newFilm);

                Film oldFilm = films.get(newFilm.getId());

                oldFilm.setName(newFilm.getName());
                log.debug("Film {} name updated to {}", newFilm.getId(), newFilm.getName());
                oldFilm.setDescription(newFilm.getDescription());
                log.debug("Film {} description updated to {}", newFilm.getId(), newFilm.getDescription());
                oldFilm.setDuration(newFilm.getDuration());
                log.debug("Film {} duration updated to {}", newFilm.getId(), newFilm.getDuration());
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
                log.debug("Film {} release date updated to {}", newFilm.getId(), newFilm.getReleaseDate());
                return oldFilm;
            } else {
                throw new NotFoundException("Film not found in storage");
            }
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            throw exception;
        }
    }

    void filmValidator(Film film) {
        if (isNameNotValid(film.getName())) {
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
    }

    boolean isNameNotValid(String name) {
        return Objects.isNull(name) || name.isBlank();
    }

    boolean isDescriptionNotValid(String description) {
        return Objects.isNull(description) || description.length() > 200;
    }

    boolean isDurationNotValid(long duration) {
        return duration <= 0;
    }

    boolean isReleaseDateNotValid(LocalDate releaseDate) {
        return Objects.isNull(releaseDate) || !releaseDate.isAfter(LocalDate.of(1895, 12, 28));
    }

    long getNextID() {
        long currentMaxID = films.values().stream()
                .mapToLong(Film::getId)
                .max()
                .orElse(0);
        return ++currentMaxID;
    }
}
