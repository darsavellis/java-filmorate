package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable long id) {
        return reviewService.getReviewById(id);
    }

    @GetMapping
    public List<Review> getReviewsByFilmId(@RequestParam(required = false) Long filmId,
                                           @RequestParam(defaultValue = "10") long count) {
        return reviewService.getReviewsByFilmId(filmId, count);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review createReview(@Valid @RequestBody Review review) {
        return reviewService.createReview(review);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public Boolean deleteReview(@PathVariable long id) {
        return reviewService.deleteReview(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public Review setLikeToReview(@PathVariable("id") long reviewId, @PathVariable long userId) {
        return reviewService.setLikeReview(reviewId, userId, 1);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public Review setDislikeToReview(@PathVariable("id") long reviewId, @PathVariable long userId) {
        return reviewService.setLikeReview(reviewId, userId, -1);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Review deleteLikeToReview(@PathVariable("id") long reviewId, @PathVariable long userId) {
        return reviewService.deleteLikeReview(reviewId, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public Review deleteDislikeToReview(@PathVariable("id") long reviewId, @PathVariable long userId) {
        return reviewService.deleteDislikeReview(reviewId, userId);
    }
}
