package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendService {
    User addFriend(long firstUserId, long secondUserId);

    User deleteFriend(long firstUserId, long secondUserId);

    List<User> getFriends(long userId);

    List<User> getCommonFriends(long firstUserId, long secondUserId);
}
