package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validation.UserValidator;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public User createUser(User user) {
        try {
            UserValidator.validate(user);
            return userStorage.createUser(user);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            throw e;
        }
    }

    public User updateUser(User newUser) {
        try {
            UserValidator.validate(newUser);
            return userStorage.updateUser(newUser);
        } catch (Exception exception) {
            log.warn(exception.getMessage(), exception);
            throw exception;
        }
    }

    public User addFriend(Long id, Long friendId) throws NotFoundException {
        checkIfUsersExist(id, friendId);
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(id);
        return friend;
    }

    public User deleteFriend(Long id, Long friendId) throws NotFoundException {
        checkIfUsersExist(id, friendId);
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
        return friend;
    }

    public Collection<User> getFriends(Long id) {
        checkIfUsersExist(id);
        return userStorage
                .getUserById(id)
                .getFriends()
                .stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public Collection<User> getCommonFriends(Long id, Long otherId) {
        checkIfUsersExist(id, otherId);
        Set<Long> firstUserFriendsIds = userStorage.getUserById(id).getFriends();
        Set<Long> secondUserFriendsIds = userStorage.getUserById(otherId).getFriends();
        return firstUserFriendsIds
                .stream()
                .filter(secondUserFriendsIds::contains)
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    void checkIfUsersExist(long... listId) {
        for (long id : listId) {
            if (!userStorage.isUserExist(id)) {
                throw new NotFoundException(String.format("User ID=%d not found", id));
            }
        }
    }
}
