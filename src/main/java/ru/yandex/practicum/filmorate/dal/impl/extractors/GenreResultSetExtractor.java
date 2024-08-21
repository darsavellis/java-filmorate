package ru.yandex.practicum.filmorate.dal.impl.extractors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class GenreResultSetExtractor implements ResultSetExtractor<Genre> {
    @Override
    public Genre extractData(ResultSet rs) throws SQLException, DataAccessException {
        rs.first();
        Genre genre = new Genre();
        genre.setId(rs.getLong("id"));
        genre.setName(rs.getString("name"));
        return genre;
    }
}
