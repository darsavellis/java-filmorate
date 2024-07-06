package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validation.FilmValidator;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film createFilm(Film film) {
        FilmValidator.validate(film);
        try {
            return filmStorage.createFilm(film);
        } catch (Exception exception) {
            log.warn(exception.getMessage(), exception);
            throw exception;
        }
    }

    public Film updateFilm(Film newFilm) {
        FilmValidator.validate(newFilm);
        try {
            return filmStorage.updateFilm(newFilm);
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            throw exception;
        }
    }

    public Film likeFilm(long filmId, long userId) {
        return editLike(filmId, userId, (likes) -> likes.add(userId));
    }

    public Film removeLike(long filmId, long userId) {
        return editLike(filmId, userId, (likes) -> likes.remove(userId));
    }

    public List<Film> getMostPopularFilms(Optional<Long> count) {
        return filmStorage.getFilms().stream()
                .sorted(Comparator.comparingInt(a -> -a.getLikes().size()))
                .limit(count.orElse(10L))
                .collect(Collectors.toList());
    }

    private Film editLike(long filmId, long userId, Consumer<Set<Long>> action) {
        if (!filmStorage.isFilmExist(filmId)) {
            throw new NotFoundException(String.format("Film id=%d not found", filmId));
        }
        if (!userStorage.isUserExist(userId)) {
            throw new NotFoundException(String.format("User id=%d not found", userId));
        }
        Film film = filmStorage.getFilmById(filmId);
        action.accept(film.getLikes());
        return film;
    }
}
