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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JdbcGenreRepository implements GenreRepository {
    static final String FIND_ALL_QUERY = "SELECT * FROM genres";
    static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE id = :id";
    static final String FIND_BY_IDS_QUERY = "SELECT * FROM genres WHERE id IN (:genres)";

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
    public Set<Genre> getByIds(List<Long> genreIds) {
        return new HashSet<>(
            jdbc.query(FIND_BY_IDS_QUERY, new MapSqlParameterSource("genres", genreIds), genreRowMapper)
        );
    }
}
