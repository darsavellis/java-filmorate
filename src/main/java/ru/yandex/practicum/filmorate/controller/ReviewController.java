package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewController {
    final ReviewService reviewService;

    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable long id) {
        return ResponseEntity
                .status(200)
                .body(reviewService.getReviewById(id));
    }

    @GetMapping
    public ResponseEntity<List<Review>> getReviewsByFilmId(@RequestParam Optional<Long> filmId,
                                                           @RequestParam(defaultValue = "10") long count) {
        return ResponseEntity
                .status(200)
                .body(reviewService.getReviewsByFilmId(filmId, count));
    }

    @PostMapping
    public ResponseEntity<Review> createReview(@Valid @RequestBody Review review) {
        return ResponseEntity
                .status(201)
                .body(reviewService.createReview(review));
    }

    @PutMapping
    public ResponseEntity<Review> updateReview(@Valid @RequestBody Review review) {
        return ResponseEntity
                .status(200)
                .body(reviewService.createReview(review));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Review> deleteReview(@PathVariable long reviewId) {
        return ResponseEntity
                .status(200)
                .body(reviewService.deleteReview(reviewId));
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Review> setLikeToReview(@PathVariable("id") long reviewId, @PathVariable long userId) {
        return ResponseEntity
                .status(200)
                .body(reviewService.setLikeReview(reviewId, userId, true));
    }

    @PutMapping("/{id}/dislike/{userId}")
    public ResponseEntity<Review> setDislikeToReview(@PathVariable("id") long reviewId, @PathVariable long userId) {
        return ResponseEntity
                .status(200)
                .body(reviewService.setLikeReview(reviewId, userId, false));
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Review> deleteLikeToReview(@PathVariable("id") long reviewId, @PathVariable long userId) {
        return ResponseEntity
                .status(200)
                .body(reviewService.deleteLikeReview(reviewId, userId));
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public ResponseEntity<Review> deleteDislikeToReview(@PathVariable("id") long reviewId, @PathVariable long userId) {
        return ResponseEntity
                .status(200)
                .body(reviewService.deleteDislikeReview(reviewId, userId));
    }

}
