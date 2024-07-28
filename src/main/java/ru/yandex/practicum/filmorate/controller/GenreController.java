package ru.yandex.practicum.filmorate.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GenreController {
    final GenreService genreService;

    @GetMapping
    public ResponseEntity<Collection<Genre>> getGenres() {
        return ResponseEntity.status(200).body(genreService.getGenres());
    }

    @GetMapping("/{genreId}")
    public ResponseEntity<Genre> getGenreById(@PathVariable long genreId) {
        return ResponseEntity.status(200).body(genreService.getGenreById(genreId));
    }
}
