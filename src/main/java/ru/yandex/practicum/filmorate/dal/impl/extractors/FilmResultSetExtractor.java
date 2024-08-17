package ru.yandex.practicum.filmorate.dal.impl.extractors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class FilmResultSetExtractor implements ResultSetExtractor<List<Film>> {
    @Override
    public List<Film> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, Film> filmMap = new LinkedHashMap<>();
        while (rs.next()) {
            Film film = new Film();
            if (!filmMap.containsKey(rs.getLong("id"))) {
                film.setId(rs.getLong("id"));
                film.setName(rs.getString("name"));
                film.setDescription(rs.getString("description"));
                film.setDuration(rs.getLong("duration"));
                LocalDate release_date = rs.getTimestamp("release_date").toLocalDateTime().toLocalDate();
                film.setReleaseDate(release_date);
                MpaRating mpaRating = new MpaRating();
                mpaRating.setId(rs.getLong("rating_id"));
                mpaRating.setName(rs.getString("rating_name"));
                mpaRating.setDescription(rs.getString("rating_description"));
                film.setMpa(mpaRating);

                filmMap.put(rs.getLong("id"), film);
            }

            try {
                if (rs.getString("genre_name") != null) {
                    Genre genre = new Genre();
                    genre.setId(rs.getLong("genre_id"));
                    genre.setName(rs.getString("genre_name"));
                    film.getGenres().add(genre);
                }

                if (rs.getString("director_name") != null) {
                    Director director = new Director();
                    director.setId(rs.getLong("director_id"));
                    director.setName(rs.getString("director_name"));
                    film.getDirectors().add(director);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return new ArrayList<>(filmMap.values());
    }
}
