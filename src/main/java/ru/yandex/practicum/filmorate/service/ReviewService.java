package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    Optional<Review> getReviewById(long reviewId);

    List<Review> getReviewsByFilmId(Optional<Long> filmId, long count);

    Review createReview(Review review);

    Review updateReview(Review review);

    Review deleteReview(long reviewId);

    Review setLikeReview(long reviewId, long userId, boolean ifPositive);

    Review deleteLikeReview(long reviewId, long userId); // удаляет и лайки и дизлайки

    Review deleteDislikeReview(long reviewId, long userId); // умеет удалять только дизлайки
}
