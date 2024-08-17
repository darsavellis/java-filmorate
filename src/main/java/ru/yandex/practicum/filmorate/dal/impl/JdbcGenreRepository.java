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
    static final String FIND_ALL_QUERY = "SELECT * FROM genres";
    static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE id = :id";
    static final String FIND_BY_IDS_QUERY = "SELECT * FROM genres WHERE id IN (:genres)";
    static final String FIND_GENRES_BY_FILM_ID_QUERY = """
        SELECT g.* FROM film_genre AS f
        JOIN genres g ON g.id = f.genre_id WHERE f.film_id = :film_id
        """;

    final NamedParameterJdbcOperations jdbc;
    final GenreRowMapper genreRowMapper;

    @Override
    public List<Genre> getGenres() {
        return jdbc.query(FIND_ALL_QUERY, genreRowMapper);
    }

    @Override
    public Optional<Genre> getGenreById(long genreId) {
        try {
            return Optional.ofNullable(
                jdbc.queryForObject(FIND_BY_ID_QUERY, new MapSqlParameterSource("id", genreId), genreRowMapper)
            );
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public List<Genre> getGenresByFilmId(long filmId) {
        return jdbc.query(FIND_GENRES_BY_FILM_ID_QUERY, Map.of("film_id", filmId), genreRowMapper);
    }


    @Override
    public List<Genre> getByIds(List<Long> genreIds) {
        return jdbc.query(FIND_BY_IDS_QUERY, new MapSqlParameterSource("genres", genreIds), genreRowMapper);

    }
}
