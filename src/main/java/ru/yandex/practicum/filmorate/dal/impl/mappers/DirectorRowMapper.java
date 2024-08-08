package ru.yandex.practicum.filmorate.dal.impl.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DirectorRowMapper implements RowMapper<Director> {
    @Override
    public Director mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Director director = new Director();
        director.setId(resultSet.getLong("id"));
        director.setName(resultSet.getString("name"));
        director.setSurname(resultSet.getString("surname"));
        return director;
    }
}
