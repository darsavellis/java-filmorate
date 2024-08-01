package ru.yandex.practicum.filmorate.dal.impl.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MpaRatingRowMapper implements RowMapper<MpaRating> {
    @Override
    public MpaRating mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        MpaRating mpaRating = new MpaRating();
        mpaRating.setId(resultSet.getLong("id"));
        mpaRating.setName(resultSet.getString("name"));
        mpaRating.setDescription(resultSet.getString("description"));
        return mpaRating;
    }
}
