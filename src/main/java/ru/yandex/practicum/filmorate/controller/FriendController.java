package ru.yandex.practicum.filmorate.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.impl.BaseFriendService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{id}/friends")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FriendController {
    final BaseFriendService friendsService;

    @PutMapping("/{friendId}")
    public User addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return friendsService.addFriend(id, friendId);
    }

    @DeleteMapping("/{friendId}")
    public User deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return friendsService.deleteFriend(id, friendId);
    }

    @GetMapping
    public Collection<User> getFriends(@PathVariable Long id) {
        return friendsService.getFriends(id);
    }

    @GetMapping("/common/{userId}")
    public Collection<User> getCommonFriends(@PathVariable Long id,
                                             @PathVariable Long userId) {
        return friendsService.getCommonFriends(id, userId);
    }
}
