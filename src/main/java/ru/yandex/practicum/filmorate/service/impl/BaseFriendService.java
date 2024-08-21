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

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class BaseFriendService implements FriendService {
    static final String USER_ID_S_NOT_FOUND = "User ID=%s not found";
    final UserRepository userRepository;

    @Override
    public User addFriend(long firstUserId, long secondUserId) throws NotFoundException {
        User firstUser = userRepository.getById(firstUserId)
            .orElseThrow(() -> new NotFoundException(String.format(USER_ID_S_NOT_FOUND, firstUserId)));
        User secondUser = userRepository.getById(secondUserId)
            .orElseThrow(() -> new NotFoundException(String.format(USER_ID_S_NOT_FOUND, secondUserId)));

        userRepository.addFriend(firstUser.getId(), secondUser.getId());
        userRepository.eventFriend(firstUser.getId(), secondUser.getId(), OperationType.ADD);
        return firstUser;
    }

    @Override
    public User deleteFriend(long firstUserId, long secondUserId) throws NotFoundException {
        User firstUser = userRepository.getById(firstUserId)
            .orElseThrow(() -> new NotFoundException(String.format(USER_ID_S_NOT_FOUND, firstUserId)));
        User secondUser = userRepository.getById(secondUserId)
            .orElseThrow(() -> new NotFoundException(String.format(USER_ID_S_NOT_FOUND, secondUserId)));

        userRepository.deleteFriend(firstUser.getId(), secondUser.getId());
        userRepository.eventFriend(firstUser.getId(), secondUser.getId(), OperationType.REMOVE);
        return firstUser;
    }

    @Override
    public List<User> getFriends(long userId) throws NotFoundException {
        User user = userRepository.getById(userId)
            .orElseThrow(() -> new NotFoundException(String.format(USER_ID_S_NOT_FOUND, userId)));

        return userRepository.getFriends(user.getId());
    }

    @Override
    public List<User> getCommonFriends(long firstUserId, long secondUserId) throws NotFoundException {
        return userRepository.getCommonFriends(firstUserId, secondUserId);
    }
}
