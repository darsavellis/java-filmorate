package ru.yandex.practicum.filmorate.dal.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.DirectorRepository;
import ru.yandex.practicum.filmorate.dal.impl.mappers.DirectorRowMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JdbcDirectorRepository implements DirectorRepository {
    static final String FIND_ALL_DIRECTORS = "SELECT * FROM directors";
    static final String FIND_BY_ID_QUERY = "SELECT * FROM directors WHERE";
    static final String INSERT_QUERY = "";
    static final String UPDATE_QUERY = "";
    static final String DELETE_QUERY = "";

    NamedParameterJdbcOperations jdbc;
    DirectorRowMapper directorRowMapper;

    @Override
    public List<Director> getAll() {
        return jdbc.query(FIND_ALL_DIRECTORS, directorRowMapper);
    }

    @Override
    public Director getById(long directorId) {
        return null;
    }

    @Override
    public Director save(Director director) {
        return null;
    }

    @Override
    public Director update(Director director) {
        return null;
    }

    @Override
    public boolean delete(long directorId) {
        return false;
    }
}
