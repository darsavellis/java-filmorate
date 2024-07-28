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
    final NamedParameterJdbcOperations jdbc;
    final GenreRowMapper genreRowMapper;

    @Override
    public List<Genre> getGenres() {
        String FIND_ALL_QUERY = "SELECT * FROM genres";

        return jdbc.query(FIND_ALL_QUERY, genreRowMapper);
    }

    @Override
    public Optional<Genre> getGenreById(long genreId) {
        String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE id = :id";

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
        String FIND_BY_IDS_QUERY = "SELECT * FROM genres WHERE id IN (:genres)";

        return new HashSet<>(
                jdbc.query(FIND_BY_IDS_QUERY, new MapSqlParameterSource("genres", genreIds), genreRowMapper)
        );
    }

}
