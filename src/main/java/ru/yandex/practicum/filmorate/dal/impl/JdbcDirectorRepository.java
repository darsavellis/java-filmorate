package ru.yandex.practicum.filmorate.dal.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.DirectorRepository;
import ru.yandex.practicum.filmorate.dal.impl.extractors.DirectorResultSetExtractor;
import ru.yandex.practicum.filmorate.dal.impl.mappers.DirectorRowMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JdbcDirectorRepository implements DirectorRepository {
    final NamedParameterJdbcOperations jdbc;
    final DirectorRowMapper directorRowMapper;
    final DirectorResultSetExtractor directorExtractor;

    @Override
    public List<Director> getAll() {
        final String findAllDirectors = "SELECT * FROM directors";

        return jdbc.query(findAllDirectors, directorRowMapper);
    }

    @Override
    public Optional<Director> getById(long directorId) {
        final String findByIdQuery = "SELECT * FROM directors WHERE id = :id";

        return Optional.ofNullable(jdbc.query(findByIdQuery, Map.of("id", directorId), directorExtractor));
    }

    @Override
    public List<Director> getByIds(List<Long> directorIds) {
        final String findByIdsQuery = "SELECT * FROM directors WHERE id IN (:directors)";

        return jdbc.query(findByIdsQuery, new MapSqlParameterSource("directors", directorIds),
            directorRowMapper);
    }

    @Override
    public Director save(Director director) {
        final String insertQuery = "INSERT INTO directors (name) VALUES (:name)";

        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameters = new MapSqlParameterSource()
            .addValue("name", director.getName());

        jdbc.update(insertQuery, parameters, generatedKeyHolder, new String[]{"id"});
        Long id = generatedKeyHolder.getKeyAs(Long.class);
        director.setId(Objects.requireNonNull(id));

        return director;
    }

    @Override
    public Director update(Director newDirector) {
        final String updateQuery = "UPDATE directors SET name = :name";

        SqlParameterSource parameters = new MapSqlParameterSource()
            .addValue("id", newDirector.getId())
            .addValue("name", newDirector.getName());

        jdbc.update(updateQuery, parameters);
        return newDirector;
    }

    @Override
    public boolean delete(long directorId) {
        final String deleteQuery = "DELETE FROM directors WHERE id = :id";

        int rows = jdbc.update(deleteQuery, Map.of("id", directorId));
        return rows > 0;
    }

    @Override
    public List<Director> getDirectorsByFilmId(long filmId) {
        final String findDirectorsByFilmId = """
            SELECT d.* FROM film_director f
            JOIN directors d ON d.id = f.director_id
            WHERE f.film_id = :film_id
            """;

        return jdbc.query(findDirectorsByFilmId, Map.of("film_id", filmId), directorRowMapper);
    }
}
