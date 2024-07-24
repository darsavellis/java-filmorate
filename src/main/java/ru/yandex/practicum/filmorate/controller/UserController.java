package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequestMapping("/users")
public class UserController {
    final UserService userService;

    @GetMapping
    public ResponseEntity<Collection<User>> getUsers() {
        return ResponseEntity
                .status(200)
                .body(userService.getUsers());
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        return ResponseEntity
                .status(201)
                .body(userService.createUser(user));
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<User> addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return ResponseEntity
                .status(200)
                .body(userService.addFriend(id, friendId));
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<User> deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return ResponseEntity
                .status(200)
                .body(userService.deleteFriend(id, friendId));
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<Collection<User>> getFriends(@PathVariable Long id) {
        return ResponseEntity
                .status(200)
                .body(userService.getFriends(id));
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<Collection<User>> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return ResponseEntity
                .status(200)
                .body(userService.getCommonFriends(id, otherId));
    }
}
