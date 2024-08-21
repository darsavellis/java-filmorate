package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.impl.BaseFilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilmController {
    final BaseFilmService filmService;

    @GetMapping
    public Collection<Film> getFilms() {
        return filmService.getFilms();
    }

    @GetMapping("/{filmId}")
    public Film getFilmById(@PathVariable long filmId) {
        return filmService.getFilmById(filmId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film likeFilm(@PathVariable long id, @PathVariable long userId) {
        return filmService.likeFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film removeLike(@PathVariable long id, @PathVariable long userId) {
        return filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> findTopPopularFilms(@RequestParam(value = "count", required = false, defaultValue = "10")
                                          Long limit,
                                          @RequestParam(required = false) Long genreId,
                                          @RequestParam(required = false) Long year) {
        return filmService.getTopPopularFilms(limit, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    public Collection<Film> getFilmsByDirector(@PathVariable long directorId,
                                               @RequestParam String sortBy) {
        return filmService.getFilmsByDirector(directorId, sortBy);
    }

    @GetMapping("/common")
    public List<Film> commonFilms(@RequestParam long userId, long friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @DeleteMapping("/{id}")
    public Film deleteFilm(@PathVariable long id) {
        return filmService.deleteFilmById(id);
    }

    @GetMapping("/search")
    public Collection<Film> searchFilm(@RequestParam String query,
                                       @RequestParam String by) {
        return filmService.searchFilms(query, by);
    }
}
