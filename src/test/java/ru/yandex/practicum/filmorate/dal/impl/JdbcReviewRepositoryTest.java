package ru.yandex.practicum.filmorate.dal.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.ReviewRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.dal.impl.extractors.FilmResultSetExtractor;
import ru.yandex.practicum.filmorate.dal.impl.extractors.ReviewResultSetExtractor;
import ru.yandex.practicum.filmorate.dal.impl.extractors.UserResultSetExtractor;
import ru.yandex.practicum.filmorate.dal.impl.mappers.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@JdbcTest
@AutoConfigureTestDatabase
@Import({JdbcReviewRepository.class, JdbcUserRepository.class, JdbcFilmRepository.class, UserRowMapper.class,
    FriendshipRowMapper.class, GenreRowMapper.class, FilmRowMapper.class, ReviewRowMapper.class, EventRowMapper.class,
    DirectorRowMapper.class, MpaRatingRowMapper.class, FilmResultSetExtractor.class, ReviewResultSetExtractor.class,
    UserResultSetExtractor.class})

@RequiredArgsConstructor(onConstructor_ = @Autowired)
class JdbcReviewRepositoryTest {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final FilmRepository filmRepository;

    @Test
    public void createUpdateDeleteReview() {
        User user1 = userRepository.getById(1).orElseThrow(() -> new NotFoundException("User don't exits"));
        User user2 = userRepository.getById(2).orElseThrow(() -> new NotFoundException("User don't exits"));
        Film film1 = filmRepository.getById(1).orElseThrow(() -> new NotFoundException("Film don't exits"));
        Film film2 = filmRepository.getById(2).orElseThrow(() -> new NotFoundException("Film don't exits"));

        Review review1 = reviewRepository.createReview(LibraryForCreatingEntities
            .getReview(user1.getId(), film1.getId()));
        Review review2 = reviewRepository.createReview(LibraryForCreatingEntities
            .getReview(user2.getId(), film2.getId()));

        assertNotNull(review1);
        assertNotNull(review2);

        Optional<Review> receivedReview = reviewRepository.getReviewById(review1.getReviewId());

        assertThat(receivedReview)
            .isPresent()
            .hasValueSatisfying(review ->
                assertThat(review).hasFieldOrPropertyWithValue("content", review1.getContent()))
            .hasValueSatisfying(review ->
                assertThat(review).hasFieldOrPropertyWithValue("userId", review1.getUserId()))
            .hasValueSatisfying(review ->
                assertThat(review).hasFieldOrPropertyWithValue("filmId", review1.getFilmId()))
            .hasValueSatisfying(review ->
                assertThat(review).hasFieldOrPropertyWithValue("isPositive", review1.getIsPositive()))
            .hasValueSatisfying(review ->
                assertThat(review).hasFieldOrPropertyWithValue("reviewId", review1.getReviewId()))
            .hasValueSatisfying(review ->
                assertThat(review).hasFieldOrPropertyWithValue("useful", review1.getUseful()));

        review1.setIsPositive(false);
        review1.setContent("Another content");
        review1.setUserId(user2.getId());
        review1.setFilmId(film2.getId());

        receivedReview = Optional.of(reviewRepository.updateReview(review1));
        assertThat(receivedReview).isPresent();
        assertEquals(review1.getIsPositive(), receivedReview.get().getIsPositive());
        assertEquals(review1.getContent(), receivedReview.get().getContent());
        assertEquals(review1.getUserId(), receivedReview.get().getUserId());
        assertEquals(review1.getFilmId(), receivedReview.get().getFilmId());

        List<Review> reviewList = reviewRepository.getAllReviews(10);
        assertEquals(2, reviewList.size(), "Оба созданных ревью ожидаются в листе");

        reviewList = reviewRepository.getAllReviews(1);
        assertEquals(1, reviewList.size(), "В листе ожидается один элемент");

        reviewRepository.deleteReview(review1.getReviewId());
        reviewList = reviewRepository.getAllReviews(10);
        assertEquals(1, reviewList.size(), "В листе ожидается один элемент");

        reviewRepository.deleteReview(review2.getReviewId());
        reviewList = reviewRepository.getAllReviews(10);
        assertEquals(0, reviewList.size(), "Ожидается пустой лист");
    }

    @Test
    public void testUsefulReview() {
        User user1 = userRepository.getById(1).orElseThrow(() -> new NotFoundException("User don't exits"));
        User user2 = userRepository.getById(2).orElseThrow(() -> new NotFoundException("User don't exits"));
        Film film1 = filmRepository.getById(1).orElseThrow(() -> new NotFoundException("Film don't exits"));
        Film film2 = filmRepository.getById(2).orElseThrow(() -> new NotFoundException("Film don't exits"));

        Review review1 = reviewRepository.createReview(LibraryForCreatingEntities
            .getReview(user1.getId(), film1.getId()));
        Review review2 = reviewRepository.createReview(LibraryForCreatingEntities
            .getReview(user2.getId(), film2.getId()));

        assertNotNull(review1);
        assertNotNull(review2);

        Optional<Review> receivedReview = reviewRepository.getReviewById(review1.getReviewId());
        assertThat(receivedReview).isPresent();
        long oldUseful = review1.getUseful();
        reviewRepository.setLikeReview(review1.getReviewId(), user1.getId(), 1);
        receivedReview = reviewRepository.getReviewById(review1.getReviewId());
        assertThat(receivedReview).isPresent();
        long newUseful = receivedReview.get().getUseful();
        assertEquals(oldUseful + 1, newUseful, "Ожидается увеличение полезности");

        oldUseful = receivedReview.get().getUseful();
        // повторный лайк не должен увеличить рейтинг
        reviewRepository.setLikeReview(review1.getReviewId(), user1.getId(), 1);
        receivedReview = reviewRepository.getReviewById(review1.getReviewId());
        assertThat(receivedReview).isPresent();
        newUseful = receivedReview.get().getUseful();
        assertEquals(oldUseful, newUseful, "Ожидается полезность на прежнем уровне");

        oldUseful = receivedReview.get().getUseful();
        reviewRepository.setLikeReview(review1.getReviewId(), user2.getId(), -1);
        receivedReview = reviewRepository.getReviewById(review1.getReviewId());
        assertThat(receivedReview).isPresent();
        newUseful = receivedReview.get().getUseful();
        assertEquals(oldUseful - 1, newUseful, "Ожидается снижение полезности");
    }

    @Test
    public void eventReview() {
        User user1 = userRepository.getById(1).orElseThrow(() -> new NotFoundException("User don't exits"));
        User user2 = userRepository.getById(2).orElseThrow(() -> new NotFoundException("User don't exits"));
        Film film1 = filmRepository.getById(1).orElseThrow(() -> new NotFoundException("Film don't exits"));
        Film film2 = filmRepository.getById(2).orElseThrow(() -> new NotFoundException("Film don't exits"));

        Review review1 = reviewRepository.createReview(LibraryForCreatingEntities
            .getReview(user1.getId(), film1.getId()));
        assertNotNull(review1);
        Review review2 = reviewRepository.createReview(LibraryForCreatingEntities
            .getReview(user2.getId(), film2.getId()));
        assertNotNull(review2);

        reviewRepository.eventReview(user1.getId(), review1.getReviewId(), OperationType.ADD);
        reviewRepository.eventReview(user1.getId(), review1.getReviewId(), OperationType.UPDATE);
        reviewRepository.eventReview(user1.getId(), review1.getReviewId(), OperationType.REMOVE);

        List<Event> eventList = userRepository.getUserEvents(user1.getId());
        assertEquals(3, eventList.size(), "Ожидается 3 события");
        assertEquals(EventType.REVIEW, eventList.get(0).getEventType(), "Ожидается событие Отзыв");
        assertEquals(OperationType.ADD, eventList.get(0).getOperation(), "Ожидается действие ADD");
        assertEquals(OperationType.UPDATE, eventList.get(1).getOperation(), "Ожидается действие UPDATE");
        assertEquals(OperationType.REMOVE, eventList.get(2).getOperation(), "Ожидается действие REMOVE");

        userRepository.delete(user1.getId());
        eventList = userRepository.getUserEvents(user1.getId());
        assertEquals(0, eventList.size(), "Ожидается отсутствие событий");
    }
}
