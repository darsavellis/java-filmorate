package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmRepository {
    List<Film> getAll();

    List<Film> getTop(long count);

    Optional<Film> getById(long filmId);

    Film save(Film film);

    Film update(Film newFilm);

    boolean delete(long filmId);

    List<Film> getCommonFilms(long userId, long friendId);
}
