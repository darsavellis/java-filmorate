package ru.yandex.practicum.filmorate.dal.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.LikeRepository;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.OperationType;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JdbcLikeRepository implements LikeRepository {

    final NamedParameterJdbcOperations jdbc;

    @Override
    public void addLike(long filmId, long userId) {
        String addLikeQuery = "INSERT INTO likes (film_id, user_id) VALUES(:film_id, :user_id)";

        jdbc.update(addLikeQuery, Map.of("film_id", filmId, "user_id", userId));
    }

    @Override
    public void removeLike(long filmId, long userId) {
        String removeLikeQuery = "DELETE FROM likes WHERE user_id = :user_id AND film_id = :film_id";

        jdbc.update(removeLikeQuery, Map.of("film_id", filmId, "user_id", userId));
    }

    @Override
    public void eventLike(long filmId, long userId, OperationType operationType) {
        String insertEventQuery = """
            INSERT INTO events (user_id, entity_id, timestamp, type_id, operation_id)
            SELECT :user_id, :entity_id, :timestamp, t.id , o.id FROM event_types t, operation_types o
            WHERE t.name = :event_type AND o.name = :operation_type""";

        Timestamp timestamp = Timestamp.from(Instant.now());
        jdbc.update(insertEventQuery, Map.of("user_id", userId, "entity_id", filmId,
            "timestamp", timestamp, "event_type", EventType.LIKE.toString(),
            "operation_type", operationType.toString()));
    }
}
