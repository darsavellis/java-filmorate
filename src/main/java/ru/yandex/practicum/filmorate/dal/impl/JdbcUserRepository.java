package ru.yandex.practicum.filmorate.dal.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.dal.impl.mappers.FriendshipRowMapper;
import ru.yandex.practicum.filmorate.dal.impl.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JdbcUserRepository implements UserRepository {
    static String FIND_ALL_USERS_QUERY = "SELECT * FROM users";
    static String FIND_USER_BY_ID_QUERY = "SELECT * FROM users WHERE id = :id";
    static String INSERT_USER_QUERY = "INSERT INTO users (email, login, name, birthday) " +
        "VALUES(:email, :login, :name, :birthday)";
    static String UPDATE_USER_QUERY = "UPDATE users SET email = :email, login = :login, name = :name," +
        " birthday = :birthday WHERE id = :id";
    static String DELETE_USER_BY_ID_QUERY = "DELETE * FROM users WHERE id = :id";
    static String FIND_USER_FRIENDS_QUERY = "SELECT (first_user_id + second_user_id - :user_id) FROM friendships f " +
        "WHERE (first_user_id = :user_id)";
    static String MODIFY_FRIEND_REQUEST_QUERY = "MERGE INTO friendships (first_user_id, second_user_id, status_id) " +
        "KEY (first_user_id, second_user_id) VALUES (:first_user_id, :second_user_id, :status_id)";
    static String DELETE_FRIEND_REQUEST_QUERY = "DELETE FROM friendships WHERE id = :id";
    static String CHECK_FRIENDSHIP_STATUS_QUERY = "SELECT * FROM friendships f " +
        "WHERE first_user_id = :first_user_id AND second_user_id = :second_user_id OR " +
        "first_user_id  = :second_user_id AND second_user_id = first_user_id";

    final NamedParameterJdbcOperations jdbc;
    final FriendshipRowMapper friendshipRowMapper;
    final UserRowMapper userRowMapper;

    @Override
    public List<User> getAll() {
        return jdbc.query(FIND_ALL_USERS_QUERY, userRowMapper);
    }

    @Override
    public Optional<User> findById(long userId) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                FIND_USER_BY_ID_QUERY,
                new MapSqlParameterSource("id", userId),
                userRowMapper
            ));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public User save(User user) {
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        SqlParameterSource userParameters = getSqlUserParameters(user);
        jdbc.update(INSERT_USER_QUERY, userParameters, generatedKeyHolder, new String[]{"id"});
        long id = generatedKeyHolder.getKeyAs(Long.class);
        user.setId(id);
        return user;
    }

    @Override
    public User update(User newUser) {
        SqlParameterSource newUserParameters = getSqlUserParameters(newUser);
        jdbc.update(UPDATE_USER_QUERY, newUserParameters);
        return newUser;
    }

    @Override
    public boolean delete(long userId) {
        int rows = jdbc.update(DELETE_USER_BY_ID_QUERY, Map.of("id", userId));
        return rows > 0;
    }

    @Override
    public Set<User> getFriends(long userId) {
        List<Long> friendIds = jdbc.queryForList(FIND_USER_FRIENDS_QUERY, Map.of("user_id", userId), Long.class);
        return friendIds
            .stream()
            .map((friendId) -> findById(friendId)
                .orElseThrow(() -> new NotFoundException(String.format("User ID=%s not found", friendId))))
            .collect(Collectors.toSet());


    }

    @Override
    public void addFriend(long firstUserId, long secondUserId) {
        Optional<Friendship> friendshipOptional = getFriendship(firstUserId, secondUserId);

        if (friendshipOptional.isPresent()) {
            Friendship friendship = friendshipOptional.get();

            if (friendship.getStatusId() == 1 && firstUserId == friendship.getReceiverId()) {
                jdbc.update(
                    MODIFY_FRIEND_REQUEST_QUERY,
                    Map.of("first_user_id", secondUserId, "second_user_id", firstUserId, "status_id", 2)
                );
            }
        } else {
            jdbc.update(
                MODIFY_FRIEND_REQUEST_QUERY,
                Map.of("first_user_id", firstUserId, "second_user_id", secondUserId, "status_id", 1)
            );
        }
    }

    @Override
    public void deleteFriend(long firstUserId, long receiverId) {
        Optional<Friendship> friendshipOptional = getFriendship(firstUserId, receiverId);

        if (friendshipOptional.isPresent()) {
            Friendship friendship = friendshipOptional.get();

            if (friendship.getStatusId() == 1 && firstUserId == friendship.getSenderId()) {
                jdbc.update(DELETE_FRIEND_REQUEST_QUERY, Map.of("id", friendship.getId()));
            } else {
                jdbc.update(
                    MODIFY_FRIEND_REQUEST_QUERY,
                    Map.of("first_user_id", firstUserId, "second_user_id", receiverId, "status_id", 1)
                );
            }
        }
    }

    @Override
    public Set<User> getCommonFriends(long firstUserId, long secondUserId) {
        Set<User> commonFriends = new HashSet<>(getFriends(firstUserId));
        commonFriends.retainAll(getFriends(secondUserId));
        return commonFriends;
    }

    private static SqlParameterSource getSqlUserParameters(User user) {
        return new MapSqlParameterSource()
            .addValue("email", user.getEmail())
            .addValue("login", user.getLogin())
            .addValue("name", user.getName())
            .addValue("birthday", user.getBirthday())
            .addValue("id", user.getId());
    }

    private Optional<Friendship> getFriendship(long senderId, long receiverId) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(CHECK_FRIENDSHIP_STATUS_QUERY,
                Map.of("first_user_id", senderId, "second_user_id", receiverId), friendshipRowMapper));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }
}
