package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreRepository {
    List<Genre> getGenres();

    Optional<Genre> getGenreById(long genreId);

    List<Genre> getGenresByFilmId(long filmId);

    List<Genre> getByIds(List<Long> genreIds);
}
