package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenreRepository {
    List<Genre> getGenres();

    Optional<Genre> getGenreById(long genreId);

    Set<Genre> getGenresByFilmId(long filmId);

    Set<Genre> getByIds(List<Long> genreIds);
}
