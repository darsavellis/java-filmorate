package ru.yandex.practicum.filmorate.dal.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.LikeRepository;

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
}
