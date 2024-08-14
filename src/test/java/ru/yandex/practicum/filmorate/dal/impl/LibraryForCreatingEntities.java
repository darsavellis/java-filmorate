package ru.yandex.practicum.filmorate.dal.impl;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class LibraryForCreatingEntities {
    public static Review getReview(long userId, long filmId) {
        Review review = new Review();
        review.setContent("The content of user " + userId + " for film " + filmId);
        review.setUserId(userId);
        review.setFilmId(filmId);
        review.setIsPositive(true);
        review.setUseful(0);
        return review;
    }

    public static User getUser(int num) {
        User user = new User();
        user.setEmail("user_" + num + "@mail.ru");
        user.setName("Name" + num);
        user.setBirthday(LocalDate.of(1998, 10, 1 + num % 30));
        user.setLogin("Login" + num);
        return user;
    }

    public static Film getFilm(int num) {
        Film film = new Film();
        film.setName("FilmName" + num);
        film.setDescription("Description for film " + num);
        film.setReleaseDate(LocalDate.now().minusYears(5).minusMonths(num % 12));
        film.setDuration(60 + num);
        MpaRating mpa = new MpaRating();
        mpa.setId(num % 5);
        film.setMpa(mpa);
        return film;
    }
}
