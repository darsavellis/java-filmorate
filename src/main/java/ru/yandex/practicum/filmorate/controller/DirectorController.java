package ru.yandex.practicum.filmorate.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DirectorController {
    final DirectorService directorService;

    @GetMapping
    @ResponseBody
    public ResponseEntity<Collection<Director>> getDirectors() {
        return ResponseEntity
            .status(200)
            .body(directorService.getDirectors());
    }

    @GetMapping("/{director-id}")
    @ResponseBody
    public ResponseEntity<Director> getDirectorById(@PathVariable("director-id") long directorId) {
        return ResponseEntity
            .status(200)
            .body(directorService.getDirectorById(directorId));
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Director> createDirector(@RequestBody Director director) {
        return ResponseEntity
            .status(201)
            .body(directorService.createDirector(director));
    }

    @PutMapping
    @ResponseBody
    public ResponseEntity<Director> updateDirector(@RequestBody Director director) {
        return ResponseEntity
            .status(201)
            .body(directorService.updateDirector(director));
    }

    @DeleteMapping("/{director-id}")
    @ResponseBody
    public ResponseEntity<Director> deleteDirector(@PathVariable("director-id") long directorId) {
        return ResponseEntity
            .status(200)
            .body(directorService.deleteDirector(directorId));
    }
}
