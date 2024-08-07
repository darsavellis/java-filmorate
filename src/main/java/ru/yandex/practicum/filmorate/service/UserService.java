package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserService {
    Collection<User> getUsers();

    User getUserById(long userId);

    User createUser(User user);

    User updateUser(User newUser);
}
