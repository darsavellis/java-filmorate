package ru.yandex.practicum.filmorate.storage.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InMemoryUserStorage implements UserStorage {
    final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public User createUser(User user) {
        user.setId(getNextID());
        log.debug("ID {} assigned to new user", user.getId());
        users.put(user.getId(), user);
        log.debug("User with ID {} is added to storage", user.getId());
        return user;
    }

    @Override
    public User removeUser(User user) {
        return users.remove(user.getId());
    }

    @Override
    public User updateUser(User newUser) {
        if (isUserExist(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            oldUser.setEmail(newUser.getEmail());
            log.debug("User {} email updated to {}", oldUser.getId(), oldUser.getEmail());
            oldUser.setLogin(newUser.getLogin());
            log.debug("User {} login updated to {}", oldUser.getId(), oldUser.getLogin());
            oldUser.setName(newUser.getName());
            log.debug("User {} name updated to {}", oldUser.getId(), oldUser.getName());
            oldUser.setBirthday(newUser.getBirthday());
            log.debug("User {} birthday updated to {}", oldUser.getId(), oldUser.getBirthday());
            return oldUser;
        } else {
            throw new NotFoundException("User not found in storage");
        }
    }

    @Override
    public User getUserById(long userId) {
        return users.get(userId);
    }

    @Override
    public boolean isUserExist(Long userId) {
        return users.containsKey(userId);
    }

    long getNextID() {
        long currentMaxID = users.values().stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0);
        return ++currentMaxID;
    }
}
