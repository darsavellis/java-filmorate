package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewService {
    Review getReviewById(long reviewId);

    List<Review> getReviewsByFilmId(Long filmId, long count);

    Review createReview(Review review);

    Review updateReview(Review review);

    boolean deleteReview(long reviewId);

    Review setLikeReview(long reviewId, long userId, int ifPositive);

    Review deleteLikeReview(long reviewId, long userId); // удаляет и лайки и дизлайки

    Review deleteDislikeReview(long reviewId, long userId); // умеет удалять только дизлайки
}
