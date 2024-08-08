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

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JdbcDirectorRepository implements DirectorRepository {
    static final String FIND_ALL_DIRECTORS = "SELECT * FROM directors";
    static final String FIND_BY_ID_QUERY = "SELECT * FROM directors WHERE id = :id";
    static final String INSERT_QUERY = "INSERT INTO directors (name, surname) VALUES (:name, :surname)";
    static final String UPDATE_QUERY = "UPDATE directors SET name = :name, surname = :surname";
    static final String DELETE_QUERY = "DELETE * FROM directors WHERE id = :id";

    NamedParameterJdbcOperations jdbc;
    DirectorRowMapper directorRowMapper;

    @Override
    public List<Director> getAll() {
        return jdbc.query(FIND_ALL_DIRECTORS, directorRowMapper);
    }

    @Override
    public Optional<Director> getById(long directorId) {
        Director director = jdbc.queryForObject(FIND_BY_ID_QUERY, Map.of("id", directorId), directorRowMapper);
        return Optional.ofNullable(director);
    }

    @Override
    public Director save(Director director) {
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("name", director.getName())
                .addValue("surname", director.getSurname());

        jdbc.update(INSERT_QUERY, parameters, generatedKeyHolder, new String[]{"id"});
        long id = generatedKeyHolder.getKeyAs(Long.class);

        director.setId(id);

        return director;
    }

    @Override
    public Director update(Director newDirector) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", newDirector.getId())
                .addValue("name", newDirector.getName())
                .addValue("surname", newDirector.getSurname());

        jdbc.update(UPDATE_QUERY, parameters);
        return newDirector;
    }

    @Override
    public boolean delete(long directorId) {
        int rows = jdbc.update(DELETE_QUERY, Map.of("id", directorId));
        return rows > 0;
    }
}
