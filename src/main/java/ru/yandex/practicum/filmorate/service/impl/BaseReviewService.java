package ru.yandex.practicum.filmorate.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class BaseReviewService implements ReviewService {

    @Override
    public Review getReviewById(long reviewId) {
        return null;
    }

    @Override
    public List<Review> getReviewsByFilmId(Optional<Long> filmId, long count) {
        return null;
    }

    @Override
    public Review createReview(Review review) {
        return null;
    }

    @Override
    public Review updateReview(Review review) {
        return null;
    }

    @Override
    public Review deleteReview(long reviewId) {
        return null;
    }

    @Override
    public Review setLikeReview(long reviewId, long userId, boolean ifPositive) {
        return null;
    }

    @Override
    public Review deleteLikeReview(long reviewId, long userId) {
        return null;
    }

    @Override
    public Review deleteDislikeReview(long reviewId, long userId) {
        return null;
    }
}
