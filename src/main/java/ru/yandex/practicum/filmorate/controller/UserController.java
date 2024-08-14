package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.impl.BaseFilmService;
import ru.yandex.practicum.filmorate.service.impl.BaseUserService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserController {
    final BaseUserService userService;
    final BaseFilmService filmService;

    @GetMapping
    public ResponseEntity<Collection<User>> getUsers() {
        return ResponseEntity
                .status(200)
                .body(userService.getUsers());
    }

    @GetMapping("/{id}/feed")
    public ResponseEntity<Collection<Event>> getEventsList(@PathVariable Long id) {
        return ResponseEntity
                .status(200)
                .body(userService.getEventsOfUser(id));
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        return ResponseEntity
                .status(201)
                .body(userService.createUser(user));
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        return ResponseEntity
                .status(200)
                .body(userService.updateUser(user));
    }

    @GetMapping("{id}/recommendations")
    public ResponseEntity<List<Film>> getRecommendations(@PathVariable("id") long userId) {
        return ResponseEntity
                .status(200)
                .body(filmService.getRecommendations(userId));
    }
}
