package ru.yandex.practicum.filmorate.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<User> addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return ResponseEntity
                .status(200)
                .body(friendsService.addFriend(id, friendId));
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<User> deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return ResponseEntity
                .status(200)
                .body(friendsService.deleteFriend(id, friendId));
    }

    @GetMapping
    public ResponseEntity<Collection<User>> getFriends(@PathVariable Long id) {
        return ResponseEntity
                .status(200)
                .body(friendsService.getFriends(id));
    }

    @GetMapping("/common/{otherId}")
    public ResponseEntity<Collection<User>> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return ResponseEntity
                .status(200)
                .body(friendsService.getCommonFriends(id, otherId));
    }
}
