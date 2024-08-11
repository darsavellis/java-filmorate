package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository {
    List<User> getAll();

    Optional<User> findById(long userId);

    User save(User film);

    User update(User newUser);

    boolean delete(long userId);

    Set<User> getFriends(long userId);

    void addFriend(long firstUserId, long secondUserId);

    void deleteFriend(long firstUserId, long secondUserId);

    Set<User> getCommonFriends(long firstUserId, long secondUserId);

}
