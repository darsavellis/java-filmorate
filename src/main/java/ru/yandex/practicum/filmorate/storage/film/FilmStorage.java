package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    public Collection<Film> getFilms();

    public Film createFilm(Film film);

    public Film removeFilm(Film film);

    public Film updateFilm(Film newFilm);

    public Film getFilmById(long filmId);

    public boolean isFilmExist(long filmId);
}
