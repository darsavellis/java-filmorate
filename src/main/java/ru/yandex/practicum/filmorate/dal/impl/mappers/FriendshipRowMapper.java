package ru.yandex.practicum.filmorate.dal.impl.mappers;


import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FriendshipRowMapper implements RowMapper<Friendship> {
    @Override
    public Friendship mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Friendship friendship = new Friendship();
        friendship.setId(resultSet.getLong("id"));
        friendship.setSenderId(resultSet.getLong("first_user_id"));
        friendship.setReceiverId(resultSet.getLong("second_user_id"));
        return friendship;
    }
}
