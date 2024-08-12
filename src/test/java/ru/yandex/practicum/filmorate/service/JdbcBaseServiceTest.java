package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.impl.JdbcFilmRepository;
import ru.yandex.practicum.filmorate.dal.impl.JdbcGenreRepository;
import ru.yandex.practicum.filmorate.dal.impl.JdbcLikeRepository;
import ru.yandex.practicum.filmorate.dal.impl.JdbcMpaRatingRepository;
import ru.yandex.practicum.filmorate.dal.impl.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.impl.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.impl.mappers.MpaRatingRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.impl.BaseFilmService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@Import({BaseFilmService.class, JdbcFilmRepository.class, FilmRowMapper.class, GenreRowMapper.class,
        JdbcMpaRatingRepository.class, MpaRatingRowMapper.class, JdbcGenreRepository.class, JdbcLikeRepository.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class JdbcBaseServiceTest {
    @Autowired
    private final BaseFilmService filmStorage;

    @Test
    void should_get_film_by_id() {
        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(1L));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film.getName().equals("New film"));
                    assertThat(film.getDescription().equals("This film is about ..."));
                    assertEquals(120, film.getDuration());
                    assertThat(film.getReleaseDate().equals(LocalDate.of(2017, 12, 28)));
                    assertThat(film.getMpa().equals(1));
                    assertEquals(1, film.getGenres().size());
                });
    }

    @Test
    void should_check_getting_all_films() {
        assertEquals(4, filmStorage.getFilms().size(), "Список не должен быть пустым");
    }

    @Test
    void should_check_the_film_was_created() {
        assertEquals(1, filmStorage.getFilmById(1L).getId(), "Фильм не был создан");
    }

    @Test
    void should_check_update_film() {
        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(3L));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film.getName().equals("updated"));
                    assertThat(film.getDescription().equals("updated"));
                    assertEquals(200, film.getDuration());
                    assertThat(film.getReleaseDate().equals(LocalDate.of(2020, 10, 20)));
                    assertThat(film.getMpa().equals(2));
                    assertEquals(0, film.getGenres().size());
                });
    }

    @Test
    void should_check_added_likes() {
        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(2L));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film.getLikes()).hasSize(3);
                });
    }

    @Test
    void should_check_deleted_likes() {
        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(1L));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film.getLikes()).hasSize(2);
                });

    }

    @Test
    void should_check_getting_the_top_films() {
        assertEquals(2, filmStorage.getMostPopularFilms(Optional.of(2L)).size(),
                "Неверное количество полученных топовых фильмов");
    }

    @Test
    void should_check_common_films() {
        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(1L));
        assertThat(filmStorage.getCommonFilms(1L, 2L).contains(filmOptional));
        List<Film> commonFilms = filmStorage.getCommonFilms(1L, 2L).stream().collect(Collectors.toList());
        assertEquals(1, commonFilms.size(), "Список не должен быть пустым");
    }

  /*  @Test
    void should_check_recommended_films() {
        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(1L));
        assertThat(filmStorage.getCommonFilms(1L).contains(filmOptional));
        List<Film> commonFilms = filmStorage.getCommonFilms(1L, 2L).stream().collect(Collectors.toList());
        assertEquals(1, commonFilms.size(), "Список не должен быть пустым");
    }*/
}
