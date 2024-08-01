package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.impl.BaseFilmService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequestMapping("/films")
public class FilmController {
    final BaseFilmService filmService;
    final String likePath = "/{id}/like";

    @GetMapping
    public ResponseEntity<Collection<Film>> getFilms() {
        return ResponseEntity
                .status(200)
                .body(filmService.getFilms());
    }

    @GetMapping("/{film-id}")
    public ResponseEntity<Film> getFilmById(@PathVariable("film-id") long filmId) {
        return ResponseEntity
                .status(200)
                .body(filmService.getFilmById(filmId));
    }

    @PostMapping
    public ResponseEntity<Film> createFilm(@Valid @RequestBody Film film) {
        return ResponseEntity
                .status(201)
                .body(filmService.createFilm(film));
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        return ResponseEntity
                .status(200)
                .body(filmService.updateFilm(film));
    }

    @PutMapping(likePath + "/{user-id}")
    public ResponseEntity<Film> likeFilm(@PathVariable long id, @PathVariable("user-id") long userId) {
        return ResponseEntity
                .status(200)
                .body(filmService.likeFilm(id, userId));
    }

    @DeleteMapping(likePath + "/{user-id}")
    public ResponseEntity<Film> removeLike(@PathVariable long id, @PathVariable("user-id") long userId) {
        return ResponseEntity
                .status(200)
                .body(filmService.removeLike(id, userId));
    }

    @GetMapping("/popular")
    @ResponseBody
    public ResponseEntity<List<Film>> getMostPopularFilms(@RequestParam Optional<Long> count) {
        return ResponseEntity
                .status(200)
                .body(filmService.getMostPopularFilms(count));
    }
}
