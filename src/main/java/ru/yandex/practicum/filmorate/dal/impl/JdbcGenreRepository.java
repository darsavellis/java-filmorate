package ru.yandex.practicum.filmorate.dal.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.impl.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JdbcGenreRepository implements GenreRepository {
    final NamedParameterJdbcOperations jdbc;
    final GenreRowMapper genreRowMapper;

    @Override
    public List<Genre> getGenres() {
        final String findAllQuery = "SELECT * FROM genres";

        return jdbc.query(findAllQuery, genreRowMapper);
    }

    @Override
    public Optional<Genre> getGenreById(long genreId) {
        final String findByIdQuery = "SELECT * FROM genres WHERE id = :id";

        try {
            return Optional.ofNullable(
                jdbc.queryForObject(findByIdQuery, new MapSqlParameterSource("id", genreId), genreRowMapper)
            );
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public List<Genre> getGenresByFilmId(long filmId) {
        final String findGenresByFilmIdQuery = """
            SELECT g.* FROM film_genre AS f
            JOIN genres g ON g.id = f.genre_id WHERE f.film_id = :film_id
            """;

        return jdbc.query(findGenresByFilmIdQuery, Map.of("film_id", filmId), genreRowMapper);
    }


    @Override
    public List<Genre> getByIds(List<Long> genreIds) {
        final String findByIdsQuery = "SELECT * FROM genres WHERE id IN (:genres)";

        return jdbc.query(findByIdsQuery, new MapSqlParameterSource("genres", genreIds), genreRowMapper);
    }
}
