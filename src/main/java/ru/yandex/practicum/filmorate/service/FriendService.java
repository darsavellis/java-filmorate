package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface FriendService {
    User addFriend(long firstUserId, long secondUserId);

    User deleteFriend(long firstUserId, long secondUserId);

    Collection<User> getFriends(long userId);

    Collection<User> getCommonFriends(long firstUserId, long secondUserId);
}
