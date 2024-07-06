package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    public Collection<User> getUsers();

    public User createUser(User user);

    public User removeUser(User user);

    public User updateUser(User user);

    public User getUserById(long userId);

    public boolean isUserExist(Long userId);
}
