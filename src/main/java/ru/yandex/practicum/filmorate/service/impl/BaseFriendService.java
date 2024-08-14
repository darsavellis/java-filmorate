package ru.yandex.practicum.filmorate.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FriendService;

import java.util.Collection;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class BaseFriendService implements FriendService {
    static final String USER_ID_S_NOT_FOUND = "User ID=%s not found";
    final UserRepository userRepository;

    public User addFriend(long firstUserId, long secondUserId) throws NotFoundException {
        User firstUser = userRepository.findById(firstUserId)
                .orElseThrow(() -> new NotFoundException(String.format(USER_ID_S_NOT_FOUND, firstUserId)));
        User secondUser = userRepository.findById(secondUserId)
                .orElseThrow(() -> new NotFoundException(String.format(USER_ID_S_NOT_FOUND, secondUserId)));

        userRepository.addFriend(firstUser.getId(), secondUser.getId());
        userRepository.eventFriend(firstUser.getId(), secondUser.getId(), OperationType.ADD);
        return firstUser;
    }

    public User deleteFriend(long firstUserId, long secondUserId) throws NotFoundException {
        User firstUser = userRepository.findById(firstUserId)
                .orElseThrow(() -> new NotFoundException(String.format(USER_ID_S_NOT_FOUND, firstUserId)));
        User secondUser = userRepository.findById(secondUserId)
                .orElseThrow(() -> new NotFoundException(String.format(USER_ID_S_NOT_FOUND, secondUserId)));

        userRepository.deleteFriend(firstUser.getId(), secondUser.getId());
        userRepository.eventFriend(firstUser.getId(), secondUser.getId(), OperationType.REMOVE);
        return firstUser;
    }

    public Collection<User> getFriends(long userId) throws NotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(USER_ID_S_NOT_FOUND, userId)));

        return userRepository.getFriends(user.getId());
    }

    public Collection<User> getCommonFriends(long firstUserId, long secondUserId) throws NotFoundException {
        return userRepository.getCommonFriends(firstUserId, secondUserId);
    }
}
