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
import ru.yandex.practicum.filmorate.dal.impl.mappers.DirectorRowMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.*;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JdbcDirectorRepository implements DirectorRepository {
    static final String FIND_ALL_DIRECTORS = "SELECT * FROM directors";
    static final String FIND_BY_ID_QUERY = "SELECT * FROM directors WHERE id = :id";
    static final String INSERT_QUERY = "INSERT INTO directors (name) VALUES (:name)";
    static final String UPDATE_QUERY = "UPDATE directors SET name = :name";
    static final String DELETE_QUERY = "DELETE FROM directors WHERE id = :id";
    static final String FIND_BY_IDS_QUERY = "SELECT * FROM directors WHERE id IN (:directors)";

    final NamedParameterJdbcOperations jdbc;
    final DirectorRowMapper directorRowMapper;

    @Override
    public List<Director> getAll() {
        return jdbc.query(FIND_ALL_DIRECTORS, directorRowMapper);
    }

    @Override
    public Optional<Director> getById(long directorId) {
        try {
            Director director = jdbc.queryForObject(FIND_BY_ID_QUERY, Map.of("id", directorId), directorRowMapper);
            return Optional.ofNullable(director);
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    @Override
    public Set<Director> getByIds(List<Long> directorIds) {
        return new HashSet<>(
            jdbc.query(FIND_BY_IDS_QUERY, new MapSqlParameterSource("directors", directorIds),
                directorRowMapper)
        );
    }

    @Override
    public Director save(Director director) {
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameters = new MapSqlParameterSource()
            .addValue("name", director.getName());

        jdbc.update(INSERT_QUERY, parameters, generatedKeyHolder, new String[]{"id"});
        Long id = generatedKeyHolder.getKeyAs(Long.class);

        if (Objects.nonNull(id)) {
            director.setId(id);
        }

        return director;
    }

    @Override
    public Director update(Director newDirector) {
        SqlParameterSource parameters = new MapSqlParameterSource()
            .addValue("id", newDirector.getId())
            .addValue("name", newDirector.getName());

        jdbc.update(UPDATE_QUERY, parameters);
        return newDirector;
    }

    @Override
    public boolean delete(long directorId) {
        int rows = jdbc.update(DELETE_QUERY, Map.of("id", directorId));
        return rows > 0;
    }
}
