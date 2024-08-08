package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository {
    Review getReviewById(long reviewId);

    List<Review> getReviewsByFilmId(long filmId, long count);

    List<Review> getAllReviews(long count);

    Review createReview(Review review);

    Review updateReview(Review review);

    Review deleteReview(long reviewId);

    Review setLikeReview(long reviewId, long userId, boolean ifPositive);

    Review deleteLikeReview(long reviewId, long userId); // удаляет и лайки и дизлайки

    Review deleteDislikeReview(long reviewId, long userId); // умеет удалять только дизлайки
}
