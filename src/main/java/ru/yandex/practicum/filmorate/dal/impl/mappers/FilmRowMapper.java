package ru.yandex.practicum.filmorate.dal.impl.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        Timestamp timestamp = resultSet.getTimestamp("release_date");
        film.setReleaseDate(timestamp.toLocalDateTime().toLocalDate());
        film.setDuration(resultSet.getLong("duration"));
        MpaRating mpaRating = new MpaRating();
        mpaRating.setId(resultSet.getLong("rating_id"));
        mpaRating.setName(resultSet.getString("rating_name"));
        mpaRating.setDescription(resultSet.getString("rating_description"));
        film.setMpa(mpaRating);
        long directorId = resultSet.getLong("director_id");
        if (directorId != 0) {
            Director director = new Director();
            director.setId(directorId);
            director.setName(resultSet.getString("director_name"));
            film.getDirectors().add(director);
        }
        return film;
    }
}
