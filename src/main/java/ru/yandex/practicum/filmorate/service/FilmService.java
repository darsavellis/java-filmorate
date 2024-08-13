package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmService {
    Collection<Film> getFilms();

    Film getFilmById(long filmId);

    Film createFilm(Film film);

    Film updateFilm(Film newFilm);

    Film likeFilm(long filmId, long userId);

    Film deleteLike(long filmId, long userId);

    List<Film> searchFilms(String query, String by);

    Collection<Film> getFilmsByDirector(long directorId, String sortBy);

    List<Film> getMostPopularFilms(long count);

    List<Film> getCommonFilms(long userId, long friendId);

    List<Film> getRecommendations(long userId);

    Film deleteFilmById(long id);

    List<Film> getTopPopularFilms(Long count, Long genreId, Long year);
}
