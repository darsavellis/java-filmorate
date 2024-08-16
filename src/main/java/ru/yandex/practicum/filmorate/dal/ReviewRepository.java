package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.OperationType;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository {
    Optional<Review> getReviewById(long reviewId);

    List<Review> getReviewsByFilmId(long filmId, long count);

    List<Review> getAllReviews(long count);

    Review createReview(Review review);

    Review updateReview(Review review);

    boolean deleteReview(long reviewId);

    void setLikeReview(long reviewId, long userId, boolean isPositive);

    void deleteLikeReview(long reviewId, long userId);

    void deleteDislikeReview(long reviewId, long userId);

    void eventReview(long userId, long reviewId, OperationType operationType);
}
