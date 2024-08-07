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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JdbcLikeRepository implements LikeRepository {
    static String FIND_ALL_LIKES_QUERY = "SELECT user_id FROM likes WHERE film_id = :film_id";

    static String ADD_LIKE_QUERY = "INSERT INTO likes (film_id, user_id) VALUES(:film_id, :user_id)";
    static String REMOVE_LIKE_QUERY = "DELETE FROM likes WHERE user_id = :user_id AND film_id = :film_id";
    static String INSERT_EVENT_QUERY = "INSERT INTO events (user_id, entity_id, timestamp, type_id, operation_id) " +
            "SELECT :user_id, :entity_id, :timestamp, t.id , o.id FROM event_types t, operation_types o " +
            "WHERE t.name = :event_type AND o.name = :operation_type";
    final NamedParameterJdbcOperations jdbc;

    @Override
    public Set<Long> getLikesByFilmId(long filmId) {
        return new HashSet<>(jdbc.queryForList(FIND_ALL_LIKES_QUERY, Map.of("film_id", filmId), Long.class));
    }

    @Override
    public void addLike(long filmId, long userId) {
        jdbc.update(ADD_LIKE_QUERY, Map.of("film_id", filmId, "user_id", userId));
    }

    @Override
    public void removeLike(long filmId, long userId) {
        jdbc.update(REMOVE_LIKE_QUERY, Map.of("film_id", filmId, "user_id", userId));
    }

    @Override
    public void eventLike(long filmId, long userId, OperationType operationType) {
        Timestamp timestamp = Timestamp.from(Instant.now());
        jdbc.update(INSERT_EVENT_QUERY, Map.of("user_id", userId, "entity_id", filmId,
                "timestamp", timestamp, "event_type", EventType.LIKE.toString(),
                "operation_type", operationType.toString()));
    }
}
