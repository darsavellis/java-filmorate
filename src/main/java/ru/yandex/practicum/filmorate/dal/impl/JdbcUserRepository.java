package ru.yandex.practicum.filmorate.dal.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.dal.impl.extractors.UserResultSetExtractor;
import ru.yandex.practicum.filmorate.dal.impl.mappers.EventRowMapper;
import ru.yandex.practicum.filmorate.dal.impl.mappers.FriendshipRowMapper;
import ru.yandex.practicum.filmorate.dal.impl.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JdbcUserRepository implements UserRepository {
    final NamedParameterJdbcOperations jdbc;
    final FriendshipRowMapper friendshipRowMapper;
    final UserRowMapper userRowMapper;
    final UserResultSetExtractor userExtractor;
    final EventRowMapper eventRowMapper;

    @Override
    public List<User> getAll() {
        String findAllUsersQuery = "SELECT * FROM users";

        return jdbc.query(findAllUsersQuery, userRowMapper);
    }

    @Override
    public Optional<User> getById(long userId) {
        String findUserByIdQuery = "SELECT * FROM users WHERE id = :id";

        return Optional.ofNullable(jdbc.query(
            findUserByIdQuery,
            new MapSqlParameterSource("id", userId),
            userExtractor
        ));
    }

    @Override
    public User save(User user) {
        String insertUserQuery = "INSERT INTO users (email, login, name, birthday) " +
            "VALUES(:email, :login, :name, :birthday)";

        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        SqlParameterSource userParameters = getSqlUserParameters(user);
        jdbc.update(insertUserQuery, userParameters, generatedKeyHolder, new String[]{"id"});
        Long id = generatedKeyHolder.getKeyAs(Long.class);
        user.setId(Objects.requireNonNull(id));
        return user;
    }

    @Override
    public User update(User newUser) {
        String updateUserQuery = "UPDATE users SET email = :email, login = :login, name = :name," +
            " birthday = :birthday WHERE id = :id";

        SqlParameterSource newUserParameters = getSqlUserParameters(newUser);
        jdbc.update(updateUserQuery, newUserParameters);
        return newUser;
    }

    @Override
    public boolean delete(long userId) {
        String deleteUserByIdQuery = "DELETE FROM users WHERE id = :id";

        int rows = jdbc.update(deleteUserByIdQuery, Map.of("id", userId));
        return rows > 0;
    }

    @Override
    public List<User> getFriends(long userId) {
        String findUserFriendsQuery = """
            SELECT u.*
            FROM friendships f
            JOIN users u ON u.id = second_user_id
            WHERE (first_user_id = :user_id)
            """;

        return jdbc.query(findUserFriendsQuery, Map.of("user_id", userId), userRowMapper);
    }

    @Override
    public void addFriend(long senderId, long receiverId) {
        String modifyFriendRequestQuery = "MERGE INTO friendships (first_user_id, second_user_id) " +
            "VALUES (:first_user_id, :second_user_id)";

        jdbc.update(
            modifyFriendRequestQuery,
            Map.of("first_user_id", senderId, "second_user_id", receiverId)
        );
    }

    @Override
    public void deleteFriend(long senderId, long receiverId) {
        String deleteFriendRequestQuery = "DELETE FROM friendships WHERE first_user_id = :first_user_id" +
            " AND second_user_id = :second_user_id";

        jdbc.update(deleteFriendRequestQuery, Map.of("first_user_id", senderId, "second_user_id", receiverId));
    }

    @Override
    public void eventFriend(long firstUserId, long secondUserId, OperationType operationType) {
        String insertEventQuery = "INSERT INTO events (user_id, entity_id, timestamp, type_id, operation_id) " +
            "SELECT :user_id, :entity_id, :timestamp, t.id , o.id FROM event_types t, operation_types o " +
            "WHERE t.name = :event_type AND o.name = :operation_type";

        Timestamp timestamp = Timestamp.from(Instant.now());
        jdbc.update(insertEventQuery, Map.of("user_id", firstUserId, "entity_id", secondUserId,
            "timestamp", timestamp, "event_type", EventType.FRIEND.toString(),
            "operation_type", operationType.toString()));
    }

    @Override
    public List<User> getCommonFriends(long firstUserId, long secondUserId) {
        Set<User> commonFriends = new HashSet<>(getFriends(firstUserId));
        commonFriends.retainAll(getFriends(secondUserId));
        return new ArrayList<>(commonFriends);
    }

    @Override
    public List<Event> getUserEvents(long userId) {
        String findUserEventsQuery = "SELECT ev.*, et.name AS event_type, ot.name AS operation_type FROM " +
            "events ev JOIN event_types et ON et.id = ev.type_id JOIN operation_types ot ON ot.id = ev.operation_id " +
            "WHERE ev.user_id = :user_id";

        return jdbc.query(findUserEventsQuery, Map.of("user_id", userId), eventRowMapper);
    }

    static SqlParameterSource getSqlUserParameters(User user) {
        return new MapSqlParameterSource()
            .addValue("email", user.getEmail())
            .addValue("login", user.getLogin())
            .addValue("name", user.getName())
            .addValue("birthday", user.getBirthday())
            .addValue("id", user.getId());
    }
}
