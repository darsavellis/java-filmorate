package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> getAll();

    Optional<User> findById(long userId);

    User save(User user);

    User update(User newUser);

    boolean delete(long userId);

    List<User> getFriends(long userId);

    void addFriend(long firstUserId, long secondUserId);

    void deleteFriend(long firstUserId, long secondUserId);

    void eventFriend(long firstUserId, long secondUserId, OperationType operationType);

    List<User> getCommonFriends(long firstUserId, long secondUserId);

    List<Event> getUserEvents(long userId);
}
