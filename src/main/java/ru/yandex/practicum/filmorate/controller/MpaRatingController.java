package ru.yandex.practicum.filmorate.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.MpaRatingService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MpaRatingController {
    final MpaRatingService mpaRatingService;

    @GetMapping
    public ResponseEntity<Collection<MpaRating>> getMpaRatings() {
        return ResponseEntity.status(200).body(mpaRatingService.getMpaRatings());
    }

    @GetMapping("/{mpaId}")
    public ResponseEntity<MpaRating> getMpaRatingById(@PathVariable long mpaId) {
        return ResponseEntity.status(200).body(mpaRatingService.getMpaRatingById(mpaId));
    }
}
