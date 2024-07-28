package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

public interface GenreService {
    Collection<Genre> getGenres();

    Genre getGenreById(long genreId);
}
