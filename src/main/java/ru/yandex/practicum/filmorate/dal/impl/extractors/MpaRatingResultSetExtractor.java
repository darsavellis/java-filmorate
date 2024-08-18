package ru.yandex.practicum.filmorate.dal.impl.extractors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MpaRatingResultSetExtractor implements ResultSetExtractor<MpaRating> {
    @Override
    public MpaRating extractData(ResultSet rs) throws SQLException, DataAccessException {
        rs.first();
        MpaRating mpaRating = new MpaRating();
        mpaRating.setId(rs.getLong("id"));
        mpaRating.setName(rs.getString("name"));
        mpaRating.setDescription(rs.getString("description"));
        return mpaRating;
    }
}
