package ru.yandex.practicum.filmorate.dal.impl.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.OperationType;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EventRowMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Event event = new Event();
        event.setId(resultSet.getLong("id"));
        event.setUserId(resultSet.getLong("user_id"));
        event.setEventType(EventType.fromString(resultSet.getString("event_type")));
        event.setOperationType(OperationType.fromString(resultSet.getString("operation_type")));
        event.setEntityId(resultSet.getLong("entity_id"));
        event.setTimestamp(resultSet.getTimestamp("timestamp"));
        return event;
    }
}
